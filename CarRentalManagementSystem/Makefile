NAME = Main
SRC = src/*.java
BIN = bin

all:
	@mkdir -p $(BIN)
	@javac -d $(BIN) $(SRC)
	@echo "Compilation finished. Run the program using 'make run'."

run:
	@java -cp $(BIN) $(NAME)

clean:
	@rm -rf $(BIN)

re: clean all

.PHONY: all run clean re