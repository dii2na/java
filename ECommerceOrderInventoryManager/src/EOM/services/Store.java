package EOM.services;

import static EOM.utils.ConsoleUtils.*;
import EOM.enums.OrderStatus;
import EOM.models.*;
import EOM.utils.Validator;
import java.util.*;

public class Store
{
    // Attributes

    private final List<Product> products;
    private final Map<Integer, Product> productsById;
    private final Map<Integer, Order> orders;
    private final Set<String> categories;
    private final Queue<Order> shippingQueue;
    private final LinkedHashMap<Integer, Order> deliveredOrders;
    private final List<Review> reviews;

    // Constructor

    public Store()
    {
        products = new ArrayList<>();
        productsById = new HashMap<>();
        orders = new HashMap<>();
        categories = new HashSet<>();
        shippingQueue = new LinkedList<>();
        deliveredOrders = new LinkedHashMap<>();
        reviews = new ArrayList<>();
    }

    // Getters

    public List<Product> getProducts()
    {
        return (List.copyOf(products));
    }

    public Map<Integer, Order> getOrders()
    {
        return (Map.copyOf(orders));
    }

    public Set<String> getCategories()
    {
        return (Set.copyOf(categories));
    }

    public Queue<Order> getShippingQueue()
    {
        return (new LinkedList<>(shippingQueue));
    }

    public LinkedHashMap<Integer, Order> getDeliveredOrders()
    {
        return (new LinkedHashMap<>(deliveredOrders));
    }

    public List<Review> getReviews()
    {
        return (List.copyOf(reviews));
    }

    // Product Management

    public Product getProductById(int productId)
    {
        return (getById(productsById, productId, "Product"));
    }

    public boolean hasProduct(int productId)
    {
        return (productsById.containsKey(productId));
    }

    public void addProduct(Product product)
    {
        Validator.validateNotNull(product, "Product cannot be null");
        checkIdExistence(productsById, product.getId(), "Product", false);
        products.add(product);
        productsById.put(product.getId(), product);
        categories.add(product.getCategory());
    }

    public void removeProduct(int productId)
    {
        checkIdExistence(productsById, productId, "Product", true);
        if (isProductInPendingOrder(productId))
            throw new IllegalStateException(
                "Product " + productId
                + " is in a PENDING order and cannot be removed");
        deleteProductEverywhere(productId);
    }

    private boolean isProductInPendingOrder(int productId)
    {
        return (orders.values().stream()
            .anyMatch(order ->
            order.isPending() && order.containsProduct(productId)));
    }

    public boolean removeOutOfStockProducts()
    {
        Iterator<Product> iterator;
        boolean removed;

        removed = false;
        iterator = products.iterator();
        while (iterator.hasNext())
        {
            Product product;

            product = iterator.next();
            if (product.getStockQuantity() == 0)
            {
                deleteProductEverywhere(iterator, product);
                removed = true;
            }
        }

        return (removed);
    }

    // Product Removal Helpers
    //
    // Removing a product keeps the listing, the ID map, and categories in
    // sync. Deleting while looping must go through the iterator.

    private void removeIfOutOfStock(Product product)
    {
        if (product.getStockQuantity() == 0)
            deleteProductEverywhere(product.getId());
    }

    private void reAddProductIfMissing(Product product)
    {
        if (!productsById.containsKey(product.getId()))
            addProduct(product);
    }

    private void deleteProductEverywhere(int productId)
    {
        Product product;

        product = productsById.remove(productId);
        if (product == null)
            return;
        products.remove(product);
        removeCategoryIfUnused(product.getCategory());
    }

    private void deleteProductEverywhere(
    Iterator<Product> iterator, Product product)
    {
        iterator.remove();
        productsById.remove(product.getId());
        removeCategoryIfUnused(product.getCategory());
    }

    private void removeCategoryIfUnused(String category)
    {
        for (Product product : products)
        {
            if (product.getCategory().equals(category))
                return;
        }
        categories.remove(category);
    }

    // Order Management

    public Order getOrderById(int orderId)
    {
        return (getById(orders, orderId, "Order"));
    }

    public boolean hasOrder(int orderId)
    {
        return (orders.containsKey(orderId));
    }

    public void addOrder(Order order)
    {
        Validator.validateNotNull(order, "Order cannot be null");
        checkIdExistence(orders, order.getOrderId(), "Order", false);
        orders.put(order.getOrderId(), order);
    }

    public void addItemToOrder(int orderId, int productId, int quantity)
    {
        Order order;
        Product product;

        order = getOrderById(orderId);
        product = getProductById(productId);
        order.addItem(product, quantity);
        removeIfOutOfStock(product);
    }

    public void removeItemFromOrder(int orderId, int productId)
    {
        Order order;
        CartItem removedItem;

        order = getOrderById(orderId);
        removedItem = order.removeItemById(productId);
        reAddProductIfMissing(removedItem.getProduct());
    }

    // Shipping

    public void addOrderToShipping(int orderId)
    {
        Order order;

        order = getOrderById(orderId);
        validateShippingOrder(order);
        order.setStatus(OrderStatus.SHIPPED);
        shippingQueue.offer(order);
    }

    public void shipNextOrder()
    {
        Order order;

        if (shippingQueue.isEmpty())
            throw new IllegalStateException(
                "There are no orders waiting to be shipped");
        order = shippingQueue.peek();
        requireItemsForShipping(order);
        order.setStatus(OrderStatus.DELIVERED);
        deliveredOrders.put(order.getOrderId(), order);
        shippingQueue.poll();
    }

    private void validateShippingOrder(Order order)
    {
        OrderStatus status;

        status = order.getStatus();
        if (status == OrderStatus.CANCELLED
                || status == OrderStatus.DELIVERED)
            throw new IllegalStateException(
                "Order #" + order.getOrderId()
                + " is " + status + " and cannot be shipped");
        requireItemsForShipping(order);
        if (shippingQueue.contains(order))
            throw new IllegalStateException(
                "Order is already in the shipping list");
    }

    private void requireItemsForShipping(Order order)
    {
        if (!order.hasItems())
            throw new IllegalStateException(
                "Order #" + order.getOrderId()
                + " has no items and cannot be shipped");
    }

    // Cancellation

    public void cancelOrder(int orderId)
    {
        Order order;

        order = getOrderById(orderId);
        order.cancel();
        shippingQueue.remove(order);
        restoreOrderProducts(order);
    }

    private void restoreOrderProducts(Order order)
    {
        order.getItems().forEach(
            item -> reAddProductIfMissing(item.getProduct()));
    }

    // Reviews

    public void addReview(int productId, String customerName, String comment)
    {
        getProductById(productId);
        reviews.add(new Review(productId, customerName, comment));
    }

    public String displayProductReviews(int productId)
    {
        List<Object[]> rows;

        rows = reviews.stream()
            .filter(review -> review.getProductId() == productId)
            .map(review -> new Object[]
            {
                review.getCustomerName(),
                review.getComment()
            })
            .toList();
        if (rows.isEmpty())
            return ("No reviews for product " + productId + ".");
        return (formatTable(
            new String[]{"Customer", "Comment"},
            rows.toArray(new Object[0][])));
    }

    public String displayAllReviews()
    {
        List<Object[]> rows;

        rows = reviews.stream()
            .map(review -> new Object[]
            {
                review.getProductId(),
                review.getCustomerName(),
                review.getComment()
            })
            .toList();
        if (rows.isEmpty())
            return ("No reviews to display.");

        return (formatTable(
            new String[]{"Product ID", "Customer", "Comment"},
            rows.toArray(new Object[0][])));
    }

    // Display

    public String displayAllProducts()
    {
        return (formatProducts(products));
    }

    public String displayProductsOrderedByPrice()
    {
        List<Product> sortedProducts;

        sortedProducts = products.stream()
            .sorted()
            .toList();
        return (formatProducts(sortedProducts));
    }

    public String showAllCategories()
    {
        StringBuilder info;

        info = new StringBuilder();
        if (categories.isEmpty())
            return ("No categories to display.");
        info.append(sectionTitle("Product Categories"));
        categories.forEach(category ->
            info.append("  • ").append(category).append(newLine()));
        return (info.toString());
    }

    public String displayAllOrders()
    {
        return (displayOrderList(
            "All Orders", "No orders available.", orders.values()));
    }

    public String displayOrdersOrderedByTotal()
    {
        List<Order> sortedOrders;

        sortedOrders = orders.values().stream()
            .sorted(Comparator.comparingDouble(Order::getTotal))
            .toList();
        return (displayOrderList(
            "Orders Ordered by Total", "No orders available.",
            sortedOrders));
    }

    public String displayShippingQueue()
    {
        return (displayOrderList(
            "Shipping Queue", "No orders waiting for shipping.",
            shippingQueue));
    }

    public String displayDeliveredOrders()
    {
        return (displayOrderList(
            "Delivered Orders", "No delivered orders.",
            deliveredOrders.values()));
    }

    private String formatProducts(List<Product> productList)
    {
        List<Object[]> rows;

        rows = productList.stream()
            .map(product -> new Object[]
            {
                product.getId(),
                product.getName(),
                money(product.getPrice()),
                product.getCategory(),
                product.getStockQuantity()
            })
            .toList();
        if (rows.isEmpty())
            return ("No products to display.");

        return (formatTable(
            new String[]{"ID", "Name", "Price", "Category", "Stock"},
            rows.toArray(new Object[0][])));
    }

    private String displayOrderList(
        String title,
        String emptyMessage,
        Collection<Order> orderList)
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle(title));
        if (orderList.isEmpty())
        {
            info.append("  • ").append(emptyMessage).append(newLine());
            return (info.toString());
        }
        orderList.forEach(info::append);

        return (info.toString());
    }

    // Lookup Helpers

    private <K, V> V getById(
        Map<K, V> map,
        K id,
        String entityName)
    {
        V value;

        value = map.get(id);
        if (value == null)
            throw new IllegalArgumentException(
                entityName + " with ID " + id + " does not exist");

        return (value);
    }

    private <K, V> void checkIdExistence(
        Map<K, V> map,
        K id,
        String entityName,
        boolean shouldExist)
    {
        boolean exists;

        exists = map.containsKey(id);
        if (shouldExist && !exists)
            throw new IllegalArgumentException(
                entityName + " with ID " + id + " does not exist");
        if (!shouldExist && exists)
            throw new IllegalArgumentException(
                entityName + " with ID " + id + " already exists");
    }
}