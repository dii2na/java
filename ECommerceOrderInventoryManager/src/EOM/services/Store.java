package EOM.services;

import static EOM.utils.ConsoleUtils.*;
import EOM.models.Order;
import EOM.models.Product;
import EOM.models.Review;
import EOM.enums.OrderStatus;
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

    // Helpers

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

    private void removeCategoryIfUnused(String category)
    {
        for (Product product : products)
        {
            if (product.getCategory().equals(category))
                return;
        }
        categories.remove(category);
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

    private String formatProducts(List<Product> productList)
    {
        List<Object[]> rows;

        rows = new ArrayList<>();

        for (Product product : productList)
        {
            rows.add(new Object[] {
                product.getId(),
                product.getName(),
                money(product.getPrice()),
                product.getCategory(),
                product.getStockQuantity()
            });
        }

        return (formatTable(
            new String[]{"ID", "Name", "Price", "Category", "Stock"},
            rows.toArray(new Object[0][])));
    }

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

    private void validateShippingOrder(Order order)
    {
        if (!order.hasItems())
            throw new IllegalStateException(
                "An order with no items cannot be shipped");
        if (shippingQueue.contains(order))
            throw new IllegalStateException(
                "Order is already in the shipping list");
    }

    // Product Management

    public Product getProductById(int productId)
    {
        return (getById(productsById, productId, "Product"));
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
        deleteProductEverywhere(productId);
    }

    public String displayAllProducts()
    {
        return (formatProducts(products));     
    }

    public String displayProductsOrderedByPrice()
    {
        List<Product> sortedProducts;

        sortedProducts = new ArrayList<>(products);
        Collections.sort(sortedProducts);

        return (formatProducts(sortedProducts));
    }
    
    public String showAllCategories()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Product Categories"));
        for (String category : categories)
            info.append("  • ").append(category).append(newLine());

        return (info.toString());
    }

    //Order Managment

    public Order getOrderById(int orderId)
    {
        return (getById(orders, orderId, "Order"));
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
        Validator.validatePositive(quantity, "Quantity");
        order.addItem(product, quantity);
        removeIfOutOfStock(product);
    }

    private void removeIfOutOfStock(Product product)
    {
        if (product.getStockQuantity() == 0)
            deleteProductEverywhere(product.getId());
    }

    public void removeItemFromOrder(int orderId, int productId)
    {
        Order order;
        Product product;

        order = getOrderById(orderId);
        product = getProductById(productId);
        order.removeItem(product);
    }

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
        order = shippingQueue.poll();
        order.setStatus(OrderStatus.DELIVERED);
        deliveredOrders.put(order.getOrderId(), order);
    }

    public void cancelOrder(int orderId)
    {
        Order order;

        order = getOrderById(orderId);
        order.cancel();
        shippingQueue.remove(order);
    }

    public void addReview(int productId, String customerName, String comment)
    {
        getProductById(productId);
        reviews.add(new Review(productId, customerName, comment));
    }

    public String displayProductReviews(int productId)
    {
        List<Object[]> rows;

        rows = new ArrayList<>();
        for (Review review : reviews)
        {
            if (review.getProductId() == productId)
            {
                rows.add(new Object[] {
                    review.getCustomerName(),
                    review.getComment()
                });
            }
        }
        if (rows.isEmpty())
            return ("No reviews for product " + productId + ".");
        return (formatTable(
            new String[]{"Customer", "Comment"},
            rows.toArray(new Object[0][])));
    }

    public void removeOutOfStockProducts()
    {
        Iterator<Product> iterator;

        iterator = products.iterator();
        while (iterator.hasNext())
        {
            Product product;

            product = iterator.next();
            if (product.getStockQuantity() == 0)
                deleteProductEverywhere(iterator, product);
        }
    }

    public String displayAllReviews()
    {
        List<Object[]> rows;

        rows = new ArrayList<>();
        for (Review review : reviews)
        {
            rows.add(new Object[] {
                review.getProductId(),
                review.getCustomerName(),
                review.getComment()
            });
        }

        return (formatTable(
            new String[]{"Product ID", "Customer", "Comment"},
            rows.toArray(new Object[0][])));
    }
    
    public String displayAllOrders()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("All Orders"));
        if (orders.isEmpty())
        {
            info.append("  • No orders available.").append(newLine());
            return (info.toString());
        }
        for (Order order : orders.values())
            info.append(order);

        return (info.toString());
    }

    public String displayShippingQueue()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Shipping Queue"));
        if (shippingQueue.isEmpty())
        {
            info.append("  • No orders waiting for shipping.").append(newLine());
            return (info.toString());
        }
        for (Order order : shippingQueue)
            info.append(order);

        return (info.toString());
    }

    public String displayDeliveredOrders()
    {
        StringBuilder info;

        info = new StringBuilder();
        info.append(sectionTitle("Delivered Orders"));
        if (deliveredOrders.isEmpty())
        {
            info.append("  • No delivered orders.").append(newLine());
            return (info.toString());
        }
        for (Order order : deliveredOrders.values())
            info.append(order);

        return (info.toString());
    }



}