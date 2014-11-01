package ehu;

import java.util.Arrays;

import ehu.ASTParser;

public class Expresion {

	public enum TypeOf {
		INT, VAR, BINARY, MONARY
	}

	private TypeOf typeOf;
	private int value;
	private String name;

	private Expresion left;
	private Expresion right;

	public Expresion(int v) {
		typeOf = TypeOf.INT;
		value = v;
		name = null;
		left = null;
		right = null;
	}

	public Expresion(String s) {
		typeOf = TypeOf.VAR;
		value = 0;
		name = s;
		left = null;
		right = null;
	}

	public Expresion(Expresion left, char opr, Expresion right) {
		typeOf = TypeOf.BINARY;
		value = opr;
		name = null;
		this.left = left;
		this.right = right;
	}

	public Expresion(char opr, Expresion exp) {
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

	public Expresion leftChild() {
		return left;
	}

	public Expresion rightChild() {
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
				throw new UnsupportedOperationException("Ilegal BinOp: " + (char) (value));
			}
		case MONARY:
			switch (value) {
			case '+':
				return left.eval();
			case '-':
				return -left.eval();
			default:
				throw new UnsupportedOperationException("Ilegal Opr: " + (char) (value));
			}
		default:
			throw new UnsupportedOperationException("Variable Exp." + name);
		}
	}

	// 1
	public static Expresion parse(String texto) {
		ASTParser astp = new ASTParser();
		return astp.parse(texto);
	}

	// 2
	public int evaluar(String[] nombres, int[] valores) {
		switch (this.typeOf) {
		case INT:
			return this.value;
		case VAR:
			return valores[(Arrays.binarySearch(nombres, this.name))];
		case BINARY:
			switch (this.value) {
			case '+':
				return this.left.evaluar(nombres, valores) + this.right.evaluar(nombres, valores);
			case '-':
				return this.left.evaluar(nombres, valores) - this.right.evaluar(nombres, valores);
			case '*':
				return this.left.evaluar(nombres, valores) * this.right.evaluar(nombres, valores);
			case '/':
				return this.left.evaluar(nombres, valores) / this.right.evaluar(nombres, valores);
			default:
				throw new UnsupportedOperationException("Ilegal BinOp: " + (char) (value));
			}
		case MONARY:
			switch (this.name) {
			case "+":
				return this.left.evaluar(nombres, valores);
			case "-":
				return -this.left.evaluar(nombres, valores);
			}
		default:
			throw new UnsupportedOperationException("Variable Exp." + name);
		}
	}

	// 3
	public int evaluar(String[] nombres) {
		return evaluarExpr(0, nombres, this) - 1;
	}

	private int evaluarExpr(int indice, String[] nombres, Expresion expresion) {
		if (expresion.typeOf == TypeOf.VAR) {
			if (!isIn(nombres, expresion.name, indice)) { // PASAR INDICE???
				nombres[indice] = expresion.name;
				indice++;
			}
		}
		if (expresion.left != null) {
			indice = evaluarExpr(indice, nombres, expresion.left);
		}
		if (expresion.right != null) {
			indice = evaluarExpr(indice, nombres, expresion.right);
		}
		return indice;
	}

	private boolean isIn(String[] lista, String key, int limite) {
		int indice = 0;
		while (indice < limite && indice < lista.length) {
			if (lista[indice] == (key)) {
				return true;
			}
			indice++;
		}
		return false;
	}

	// 4
	public int contarSubexpresion(String texto) {
		return contarSubexpresion(parse(texto), 0);
	}

	@SuppressWarnings("unused")
	private int contarSubexpresion(Expresion exp, int contador) {
		if (this != null) {
			if (this.typeOf != exp.typeOf) {
				contador = 0;
				if (this.left != null) {
					contador = contador + this.left.contarSubexpresion(exp, contador);
				}
				if (this.right != null) {
					contador = contador + this.right.contarSubexpresion(exp, contador);
				}
				return contador;
			} else {
				switch (this.typeOf) {
				case INT:
					if (this.value == exp.value) {
						return 1;
					} else {
						return 0;
					}

				case VAR:
					if (this.name.equalsIgnoreCase(exp.name)) {
						return 1;
					} else {
						return 0;
					}

				case BINARY:
					if ((this.value == exp.value) && (this.left.contarSubexpresion(exp.left, contador) == 1)
							&& (this.right.contarSubexpresion(exp.right, contador) == 1)) {
						return 1;
					} else {
						return this.left.contarSubexpresion(exp, contador) + this.right.contarSubexpresion(exp, contador);
					}

				case MONARY:
					if ((this.value == exp.value) && (this.left.contarSubexpresion(exp.left, contador) == 1)) {
						return 1;
					} else {
						return this.left.contarSubexpresion(exp, contador);
					}

				default:
					throw new UnsupportedOperationException("Ilegal Exp.");
				}

			}
		}
		return 0;
	}

	public int substituirSubexpresion(String texto, String nombre) {
		return substituirSubexpresion(parse(texto), nombre, 0);
	}

	@SuppressWarnings("unused")
	private int substituirSubexpresion(Expresion exp, String nombre, int contador) {
		if (this != null) {
			if (this.typeOf != exp.typeOf) {
				contador = 0;
				if (this.left != null) {
					contador = contador + this.left.substituirSubexpresion(exp, nombre, contador);
				}
				if (this.right != null) {
					contador = contador + this.right.substituirSubexpresion(exp, nombre, contador);
				}
				return contador;
			} else {
				switch (this.typeOf) {
				case INT:
					if (this.value == exp.value) {
						this.modificar(nombre);
						return 1;
					} else {
						return 0;
					}

				case VAR:
					if (this.name.equalsIgnoreCase(exp.name)) {
						this.modificar(nombre);
						return 1;
					} else {
						return 0;
					}

				case BINARY:
					if ((this.value == exp.value) && (this.left.contarSubexpresion(exp.left, contador) == 1)
							&& (this.right.contarSubexpresion(exp.right, contador) == 1)) {
						this.modificar(nombre);
						return 1;
					} else {
						return this.left.substituirSubexpresion(exp, nombre, contador) + this.right.substituirSubexpresion(exp, nombre, contador);
					}

				case MONARY:
					if ((this.value == exp.value) && (this.left.contarSubexpresion(exp.left, contador) == 1)) {
						this.modificar(nombre);
						return 1;
					} else {
						return this.left.substituirSubexpresion(exp, nombre, contador);
					}

				default:
					throw new UnsupportedOperationException("Ilegal Exp.");
				}

			}
		}
		return 0;
	}

	private void modificar(String nombre) {
		typeOf = TypeOf.VAR;
		value = 0;
		name = nombre;
		left = null;
		right = null;
	}
	
	public String imprimir(){
		  if(this.typeOf() == TypeOf.INT){
		   return Integer.toString(this.value);
		  }
		  if(this.typeOf() == TypeOf.VAR){
		   return this.name;
		  }
		  if(this.typeOf() == TypeOf.BINARY){
		   switch(value){
		   case '+':return("(" + this.left.imprimir() + ")" + " + " + "(" + this.right.imprimir() + ")");

		   case '-': return("(" + this.left.imprimir() + ")" + " - " + "(" + this.right.imprimir() + ")");

		   case '*': return("(" + this.left.imprimir() + ")" + " * " + "(" + this.right.imprimir() + ")");

		   case '/': return("(" + this.left.imprimir() + ")" + " / " + "(" + this.right.imprimir() + ")");

		   default:
		    throw new UnsupportedOperationException("Ilegal BinOp: " + (char)(value));
		   }
		  }
		  if(this.typeOf() == TypeOf.MONARY){
		   switch (value) {
		   case '+':
		    return( " + " +"( " + this.left.imprimir() + " )");
		   case '-':
		    return( " - " +"( " + this.left.imprimir() + " )");
		   default:
		    throw new UnsupportedOperationException("Ilegal Opr: " + (char)(value));
		   }
		  }
		  else{
		   throw new UnsupportedOperationException("Ilegal Exp + name");
		  }
		 }

	public static void main(String[] args) {
		String texto1 = "( 5 + 2 ) * ( x - y ) - 6";
		String texto2 = "2 + 2";
		String texto3 = "x + y + z / 2";
		String expresion = "( ( ( 5 + 3 ) + ( 2 - 2 ) ) * ( ( 2 - + 2 ) - ( 5 + 3 ) ) ) + ( 2 - + 2 )"; 
		// "( ( 5 + 3 ) + ( 2 - 2 ) ) * ( ( 2 + 2 ) - ( 5 + ( 2 + 2 ) ) ) + ( 2 + 2 )";

		String[] var = new String[] { "x", "y", "z" };
		int[] valor = new int[] { 1, -3, 6 };

		String[] nuevo = new String[4];

		Expresion exp1 = parse(texto1);
		Expresion exp2 = parse(texto2);
		Expresion exp3 = parse(texto3);
		Expresion exp4 = parse(expresion);

		System.out.println("exp1;");
		System.out.println(exp1.evaluar(var, valor));
		System.out.println();
		
		System.out.println(exp2.imprimir());
		
		System.out.println("exp3;");
		System.out.println(exp3.imprimir());
		System.out.println(exp3.evaluar(var, valor));
		System.out.println();

		System.out.println("variables exp3;");
		System.out.println("\t" +exp3.evaluar(nuevo));
		System.out.println("\t" + nuevo[0]);
		System.out.println("\t" +nuevo[1]);
		System.out.println("\t" +nuevo[2]);
		System.out.println("\t" +nuevo[3]);
		System.out.println();
		System.out.println();

		System.out.println("prueba exp4--4;");
		System.out.println(exp4.contarSubexpresion("2 - + 2"));
		System.out.println();

		System.out.println("prueba exp4--5;");
		System.out.println("Expresion SIN sustituir: ");
		System.out.println(exp4.imprimir());
		System.out.println(exp4.substituirSubexpresion("2 - + 2", "z"));
		System.out.println("Expresion CON Sustitucion: ");
		System.out.println(exp4.imprimir());

	}
}
