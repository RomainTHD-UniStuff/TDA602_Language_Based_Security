all:
	javac src/backend/*.java -sourcepath src -d build
	javac src/ShoppingCart.java -sourcepath src -d build

resetFiles:
	rm -f res/backend/wallet.txt
	rm -f res/backend/pocket.txt
	echo 30000 > res/backend/wallet.txt
	touch res/backend/pocket.txt

clean: resetFiles
	rm -rf build
