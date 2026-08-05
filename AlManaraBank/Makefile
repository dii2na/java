NAME = bank.Main
SRC = $(shell find src -name '*.java')
BIN = bin
JAVAC = javac
JAVA = java

all:
	@mkdir -p $(BIN)
	@$(JAVAC) -d $(BIN) $(SRC)
	@echo "Compilation finished. Run the program using 'make run'."

run:
	@$(JAVA) -cp $(BIN) $(NAME)

clean:
	@rm -rf $(BIN)

re: clean all

.PHONY: all run clean re
