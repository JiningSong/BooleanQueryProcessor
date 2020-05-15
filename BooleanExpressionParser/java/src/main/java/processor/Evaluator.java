package processor;

import base.Operator;
import base.Symbols;
import base.Token;
import base.TokenType;
import exception.ExceptionCollection;
import util.MathUtil;
import util.OperatorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Evaluator {
    public static void main(String[] args) {
        List<Token> tokens = Lexer.tokenize("term1&(term3|term2)");
        System.err.println(String.format("[Evaluator:tokens]: %s", tokens));
        //tokens = Lexer.tokenize("*3(*-2!3)");
        List<Token> transformedTokens = Parser.transformToPostFix(tokens);
        System.err.println(String.format("[Evaluator:transformedTokens]: %s", transformedTokens));
        ArrayList<String> result = (ArrayList<String>)evaluate(transformedTokens);
        System.out.println(result.toString());
    }

    //stack-based evaluation of RPN expression
    public static ArrayList<String> evaluate(List<Token> tokens) {

        // Generate list of query terms
        ArrayList<String> queryTerms = new ArrayList<String>();

        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).getType()==TokenType.TERM){
                queryTerms.add((String)tokens.get(i).getValue());
            }
        }

        System.err.println(String.format("[Evaluator:queryTerms]: %s", queryTerms.toString()));

        // Fake term_dict (TODO: replace this by search result from InvertedSearch)
        HashMap<String, List<String>> term_dict = new HashMap<String, List<String>>();
        term_dict.put("term1", new ArrayList<String>(Arrays.asList("id_0", "id_1", "id_2")));
        term_dict.put("term2", new ArrayList<String>(Arrays.asList("id_3", "id_4", "id_5")));
        term_dict.put("term3", new ArrayList<String>(Arrays.asList("id_0", "id_1", "id_6")));
        System.err.println(String.format("[Evaluator:term dict]: %s", term_dict.toString()));

        Stack<Token> evalStack = new Stack<>();

        for(Token curToken : tokens) { 
            System.err.println(String.format("[Evaluator:currToken]: %s", curToken.toString()));
            //push number to stack
            if(curToken.getType() == TokenType.TERM) {
                Token queryResult = new Token<>(term_dict.get(curToken.toString()), TokenType.LIST);
                evalStack.push(queryResult);
                //operator handling
            } else {
                char operatorSymbol = (char)curToken.getValue();
                System.err.println(String.format("[Evaluator:operatorSymbol]: %c", operatorSymbol));

                //if binary operator => 2 numbers needed, unary only needs 1 number
                if(evalStack.size() < 2 && Symbols.BI_OPERATORS.contains(operatorSymbol)
                        || evalStack.size() < 1 && Symbols.UNARY_OPERATORS.contains(operatorSymbol)) {
                    throw new ExceptionCollection.EvaluatorException("No number found to map to operator.");
                } else if(Symbols.BI_OPERATORS.contains(operatorSymbol)) {
                    //operation applies to top two elements
                    Token token1 = evalStack.pop();
                    Token token2 = evalStack.pop();

                    System.err.println(String.format("[Evaluator:token 1 (list1)]: %s", token1.toString()));
                    System.err.println(String.format("[Evaluator:token 2 (list2)]: %s", token2.toString()));


                    Operator.VarArgsFunction operation = Objects.requireNonNull(
                            OperatorUtil.getOperator((Character) curToken.getValue())).getOperation();

                    System.err.println(String.format("[Evaluator:operation==null]: %b", operation==null));

                    //push result back to stack
                    if (operation == null && (Character)curToken.getValue() == '&'){
                        ((ArrayList<String>) token1.getValue()).retainAll(((ArrayList<String>)token2.getValue()));
                        System.err.println(String.format("[Evaluator:intersection]: %s", token1.getValue().toString()));
                        evalStack.push(new Token<>((ArrayList<String>)token1.getValue(), TokenType.LIST));  
                    }
                    else if (operation == null && (Character)curToken.getValue() == '|'){
                        ((ArrayList<String>) token1.getValue()).removeAll(((ArrayList<String>)token2.getValue()));
                        ((ArrayList<String>) token1.getValue()).addAll(((ArrayList<String>)token2.getValue()));
                        System.err.println(String.format("[Evaluator:union]: %s", token1.getValue().toString()));
                        evalStack.push(new Token<>((ArrayList<String>)token1.getValue(), TokenType.LIST)); 
                    }


                } else if(Symbols.UNARY_OPERATORS.contains(operatorSymbol)) {
                    //operation applies to top element
                    Token number = evalStack.pop();
                    Operator.VarArgsFunction operation = Objects.requireNonNull(
                            OperatorUtil.getOperator((Character) curToken.getValue())).getOperation();
                    //push result back to stack
                    evalStack.push(new Token<>(operation.apply((Double)number.getValue()), TokenType.NUMBER));
                }
            }
        }
        //numbers left over
        if(evalStack.size() > 1) {
            throw new ExceptionCollection.EvaluatorException("No operator found to map to number.");
        }
        //only value in stack is result, round it to given digit number
        return (ArrayList<String>)evalStack.pop().getValue();
    }
}
