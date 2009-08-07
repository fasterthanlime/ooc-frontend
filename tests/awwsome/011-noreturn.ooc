include stdio;
extern func printf(String, ...);
cover String from char*;
cover Int from int;

func message -> String "koolaid\n";
func main printf message;
