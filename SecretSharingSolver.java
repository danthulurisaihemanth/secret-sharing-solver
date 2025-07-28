import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class SecretSharingSolver {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("Usage: java SecretSharingSolver <input.json>");
                System.exit(1);
            }
            
            JsonNode input = mapper.readTree(new File(args[0]));
            BigInteger secret = findSecret(input);
            System.out.println(secret);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static BigInteger findSecret(JsonNode input) throws IOException {
        int n = input.get("keys").get("n").asInt();
        int k = input.get("keys").get("k").asInt();
        
        Map<Integer, BigInteger> allShares = new HashMap<>();
        
        for (int i = 1; i <= n; i++) {
            String key = String.valueOf(i);
            if (input.has(key)) {
                JsonNode shareNode = input.get(key);
                int base = shareNode.get("base").asInt();
                String value = shareNode.get("value").asText();
                
                BigInteger y = evaluateFunction(value, BigInteger.valueOf(i));
                allShares.put(i, y);
            }
        }
        
        return findCorrectSecret(allShares, k);
    }
    
    private static BigInteger evaluateFunction(String expression, BigInteger x) {
        expression = expression.trim();
        
        if (expression.matches("\\d+")) {
            return new BigInteger(expression);
        }
        
        if (expression.contains("multiply")) {
            return evaluateMultiply(expression, x);
        } else if (expression.contains("gcd")) {
            return evaluateGcd(expression, x);
        } else if (expression.contains("lcm")) {
            return evaluateLcm(expression, x);
        } else if (expression.contains("add")) {
            return evaluateAdd(expression, x);
        } else if (expression.contains("subtract")) {
            return evaluateSubtract(expression, x);
        } else if (expression.contains("power")) {
            return evaluatePower(expression, x);
        } else if (expression.contains("x")) {
            return evaluatePolynomial(expression, x);
        }
        
        return new BigInteger(expression);
    }
    
    private static BigInteger evaluateMultiply(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "multiply");
        String[] parts = content.split(",");
        BigInteger result = BigInteger.ONE;
        
        for (String part : parts) {
            part = part.trim().replace("x", x.toString());
            result = result.multiply(new BigInteger(part));
        }
        return result;
    }
    
    private static BigInteger evaluateGcd(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "gcd");
        String[] parts = content.split(",");
        BigInteger result = new BigInteger(parts[0].trim().replace("x", x.toString()));
        
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim().replace("x", x.toString());
            result = result.gcd(new BigInteger(part));
        }
        return result;
    }
    
    private static BigInteger evaluateLcm(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "lcm");
        String[] parts = content.split(",");
        BigInteger result = new BigInteger(parts[0].trim().replace("x", x.toString()));
        
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim().replace("x", x.toString());
            BigInteger b = new BigInteger(part);
            result = result.multiply(b).divide(result.gcd(b));
        }
        return result;
    }
    
    private static BigInteger evaluateAdd(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "add");
        String[] parts = content.split(",");
        BigInteger result = BigInteger.ZERO;
        
        for (String part : parts) {
            part = part.trim().replace("x", x.toString());
            result = result.add(new BigInteger(part));
        }
        return result;
    }
    
    private static BigInteger evaluateSubtract(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "subtract");
        String[] parts = content.split(",");
        BigInteger result = new BigInteger(parts[0].trim().replace("x", x.toString()));
        
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim().replace("x", x.toString());
            result = result.subtract(new BigInteger(part));
        }
        return result;
    }
    
    private static BigInteger evaluatePower(String expr, BigInteger x) {
        String content = extractFunctionContent(expr, "power");
        String[] parts = content.split(",");
        BigInteger base = new BigInteger(parts[0].trim().replace("x", x.toString()));
        int exponent = Integer.parseInt(parts[1].trim().replace("x", x.toString()));
        return base.pow(exponent);
    }
    
    private static BigInteger evaluatePolynomial(String expr, BigInteger x) {
        expr = expr.replace("x", x.toString());
        return new BigInteger(expr);
    }
    
    private static String extractFunctionContent(String expr, String functionName) {
        int start = expr.indexOf(functionName + "(") + functionName.length() + 1;
        int end = expr.lastIndexOf(")");
        return expr.substring(start, end);
    }
    
    private static BigInteger findCorrectSecret(Map<Integer, BigInteger> shares, int k) {
        List<Integer> keys = new ArrayList<>(shares.keySet());
        Map<BigInteger, Integer> secretCounts = new HashMap<>();
        
        for (List<Integer> combination : combinations(keys, k)) {
            Map<Integer, BigInteger> subset = new HashMap<>();
            for (Integer key : combination) {
                subset.put(key, shares.get(key));
            }
            
            BigInteger secret = reconstructSecret(subset);
            secretCounts.put(secret, secretCounts.getOrDefault(secret, 0) + 1);
        }
        
        return secretCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(BigInteger.ZERO);
    }
    
    private static List<List<Integer>> combinations(List<Integer> list, int k) {
        List<List<Integer>> result = new ArrayList<>();
        generateCombinations(list, k, 0, new ArrayList<>(), result);
        return result;
    }
    
    private static void generateCombinations(List<Integer> list, int k, int start, 
                                           List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i));
            generateCombinations(list, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    private static BigInteger reconstructSecret(Map<Integer, BigInteger> shares) {
        List<Integer> xValues = new ArrayList<>(shares.keySet());
        BigInteger secret = BigInteger.ZERO;
        
        for (int i = 0; i < xValues.size(); i++) {
            int xi = xValues.get(i);
            BigInteger yi = shares.get(xi);
            BigInteger li = lagrangeBasis(xValues, i);
            secret = secret.add(yi.multiply(li));
        }
        
        return secret;
    }
    
    private static BigInteger lagrangeBasis(List<Integer> xValues, int i) {
        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;
        int xi = xValues.get(i);
        
        for (int j = 0; j < xValues.size(); j++) {
            if (i != j) {
                int xj = xValues.get(j);
                numerator = numerator.multiply(BigInteger.valueOf(-xj));
                denominator = denominator.multiply(BigInteger.valueOf(xi - xj));
            }
        }
        
        return numerator.divide(denominator);
    }
}