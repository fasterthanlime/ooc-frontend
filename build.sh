cc=-tcc
flags=-t -r -dyngc $cc

cd tests/
for dir in $(ls); do
	cd $dir
	for file in $(find ./ -name "*.ooc"); do
		ooc -sourcepath=$dir/ $file $flags
		code=$?
		if [[ code -ne 0 ]]; then
			exit
		fi
	done
done
