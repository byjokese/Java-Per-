package ehu;

import java.util.Scanner;

public class ASTParser {
	private Scanner scanner;
	private LexSymbol current;

	public Expresion parse(String source) {
		scanner = new Scanner(source);
		nextSymbol();
		return parseExp();
	}

	private Expresion parseExp() {
		Expresion resultExp = parseMultiplicativeExp();
		while (current != null && current.isAdditive()) {
			LexSymbol symb = current;
			nextSymbol();
			Expresion e = parseMultiplicativeExp();
			resultExp = new Expresion(resultExp, (char)(symb.value), e);
		}
		return resultExp;
	}

	private Expresion parseMultiplicativeExp() {
		Expresion resultExp = parseUnaryExp();
		while (current != null && current.isMultiplicative()) {
			LexSymbol symb = current;
			nextSymbol();
			Expresion e = parseUnaryExp();
			resultExp = new Expresion(resultExp, (char)(symb.value), e);
		}
		return resultExp;
	}

	private Expresion parseUnaryExp() {
		if (current != null && current.isUnary()) {
			LexSymbol symb = current;
			nextSymbol();
			Expresion e = parseUnaryExp();
			return new Expresion((char)(symb.value), e);
		} else {
			return parsePrimaryExp();
		}
	}

	private Expresion parsePrimaryExp() {
		if (current == null) {
			throw new RuntimeException("Unexpected end of input");			
		}
		switch (current.typeOf) {
		case INT: 
			int v = current.value;
			nextSymbol();			
			return new Expresion(v);
		case VAR: 
			String s = current.name;
			nextSymbol();			
			return new Expresion(s);
		case OPR: 
			if (current.value != '(') {
				throw new RuntimeException("Expected (" + current.name);
			}
			nextSymbol();
			Expresion e = parseExp();
			if (current.typeOf != TypeOfSymbol.OPR || current.value != ')') {
				throw new RuntimeException("Expected )" + current.name);
			}
			nextSymbol();
			return e;
		default:
			return null;
		}
	}

	public enum TypeOfSymbol {
		INT, VAR, OPR
	};

	private static class LexSymbol {
		private final TypeOfSymbol typeOf;
		private final int value;
		private final String name;

		public LexSymbol(int v) {
			typeOf = TypeOfSymbol.INT;
			value = v;
			name = null;
		}

		public LexSymbol(String s) {
			typeOf = TypeOfSymbol.VAR; 
			value = 0;
			name = s;
		}
		
		public LexSymbol(char c) {
			typeOf = TypeOfSymbol.OPR; 
			value = c;
			name = null;
		}
		
		public boolean isAdditive() {
			return (typeOf == TypeOfSymbol.OPR) && (value == '+' || value == '-');
		}
		
		public boolean isMultiplicative() {
			return (typeOf == TypeOfSymbol.OPR) && (value == '*' || value == '/');
		}
		
		public boolean isUnary() {
			return (typeOf == TypeOfSymbol.OPR) && (value == '+' || value == '-');
		}
		
		public static boolean isOperator(String s) {
			if (s.length() > 1 ) return false;
			char c = s.charAt(0);
			return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')'; 
		}

		public static boolean isIdentifier(String s) {
			for (int i = 0; i < s.length(); i++) {
				if (!Character.isJavaIdentifierPart(s.charAt(i)))
					return false;
						
			}
			return true;
		}
	}

	private void nextSymbol() {
		if (!scanner.hasNext()) {
			current = null;
		} else if (scanner.hasNextInt()) {
			current = new LexSymbol(scanner.nextInt());
		} else {
			String s = scanner.next();
			if (LexSymbol.isOperator(s)) {
				current = new LexSymbol(s.charAt(0));
			} else {
				if (!LexSymbol.isIdentifier(s))
					throw new RuntimeException("Bad symbol" + s);
				current = new LexSymbol(s);
			}
		}
	}
}