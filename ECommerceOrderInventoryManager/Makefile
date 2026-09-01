JAVAC := javac
JAVA := java
BUILD_DIR := build
MAIN_CLASS := EOM.Main
SRC_FILES := $(shell find src -type f -name '*.java')

all: compile

compile:
	@mkdir -p $(BUILD_DIR)
	@$(JAVAC) -d $(BUILD_DIR) $(SRC_FILES)
	@echo Done.

run: compile
	@$(JAVA) -cp $(BUILD_DIR) $(MAIN_CLASS)

re: clean compile
	@echo Rebuilt from scratch.

clean:
	@rm -rf $(BUILD_DIR)
	@echo Removed $(BUILD_DIR)/

.PHONY: all compile run re clean