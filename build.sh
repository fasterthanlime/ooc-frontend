flags="-t -r"

cd tests/
for dir in $(ls); do
	cd $dir
	for file in $(find ./ -name "*.ooc"); do
		ooc -sourcepath=$dir/ $file -t -r -tcc -dyngc
		code=$?
		if [[ code -ne 0 ]]; then
			exit
		fi
	done
done
