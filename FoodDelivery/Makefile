JAVAC = javac
JAVA = java

SRC_DIR = src
OUT_DIR = out
MAIN_CLASS = fooddelivery.console.Main

SOURCES = $(shell find $(SRC_DIR) -name "*.java")

all:
	@echo "Compiling..."
	@$(JAVAC) -nowarn -d $(OUT_DIR) $(SOURCES)
	@echo "Build complete"

run: all
	@echo "Starting MASR Delivery..."
	@$(JAVA) -cp $(OUT_DIR) $(MAIN_CLASS)

re:
	@echo "Rebuilding from scratch..."
	@rm -rf $(OUT_DIR)
	@$(JAVAC) -nowarn -d $(OUT_DIR) $(SOURCES)
	@echo "Build complete"

clean:
	@echo "Cleaning..."
	@rm -rf $(OUT_DIR)
	@echo "Clean complete"

fclean: clean
	@echo "Removing IntelliJ metadata..."
	@rm -rf .idea out/*.iml *.iml
	@echo "Full clean complete"

.PHONY: all run re clean fclean