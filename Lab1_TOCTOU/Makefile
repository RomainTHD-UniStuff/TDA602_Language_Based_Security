.PHONY: all resetFiles clean

all: res/backend/pocket.txt res/backend/wallet.txt
	javac src/backend/*.java -sourcepath src -d build
	javac src/ShoppingCart.java -sourcepath src -d build

res/backend/pocket.txt:
	touch res/backend/pocket.txt

res/backend/wallet.txt:
	echo 30000 > res/backend/wallet.txt

resetFiles:
	rm -f res/backend/wallet.txt
	rm -f res/backend/pocket.txt

clean: resetFiles
	rm -rf build
