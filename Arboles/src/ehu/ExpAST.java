package ehu;

public class ExpAST {
	public enum TypeOf {
		INT, VAR, BINARY, MONARY
	}

	private final TypeOf typeOf;
	private final int value;
	private final String name;

	private final ExpAST left;
	private final ExpAST right;

	public ExpAST(int v) {
		typeOf = TypeOf.INT;
		value = v;
		name = null;
		left = null;
		right = null;
	}

	public ExpAST(String s) {
		typeOf = TypeOf.VAR;
		value = 0;
		name = s;
		left = null;
		right = null;
	}

	public ExpAST(ExpAST left, char opr, ExpAST right) {
		typeOf = TypeOf.BINARY;
		value = opr;
		name = null;
		this.left = left;
		this.right = right;
	}

	public ExpAST(char opr, ExpAST exp) {
		typeOf = TypeOf.MONARY;
		value = opr;
		name = null;
		this.left = exp;
		this.right = null;
	}
	
	public TypeOf typeOf() {
		return typeOf;
	}
	
	public int value() {
		return value;
	}
	
	public String variableName() {
		return name;
	}
	
	public ExpAST leftChild() {
		return left;
	}

	public ExpAST rightChild() {
		return right;
	}

	public int eval() {
		switch (typeOf) {
		case INT:
			return value;
		case BINARY:
			switch (value) {
			case '+':
				return left.eval() + right.eval();
			case '-':
				return left.eval() - right.eval();
			case '*':
				return left.eval() * right.eval();
			case '/':
				return left.eval() / right.eval();
			default:
				throw new UnsupportedOperationException("Ilegal BinOp: " + (char)(value));
			}
		case MONARY:
			switch (value) {
			case '+':
				return left.eval();
			case '-':
				return -left.eval();
			default:
				throw new UnsupportedOperationException("Ilegal Opr: " + (char)(value));
			}
		default:
			throw new UnsupportedOperationException("Variable Exp." + name);
		}
	}


	public static void main(String[] args) {
		ASTParser parser = new ASTParser();
		System.out.println(parser.parse("1 + 2 * 3 + 5").eval());
		System.out.println(parser.parse("( 1 + 2 ) * ( 3 + 5 )").eval());
		System.out.println(parser.parse(" - ( 1 + 2 ) * - - ( 3 + 5 )").eval());
		System.out.println(parser.parse("+ - + 1").eval());
	}	
}
