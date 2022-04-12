.PHONY: all resetFiles reset clean

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

reset: | resetFiles res/backend/wallet.txt res/backend/pocket.txt ;

clean: reset
	rm -rf build
