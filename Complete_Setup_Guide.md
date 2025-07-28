# How to Run the Secret Sharing Solver

## Quick Start (5 Minutes)

### Step 1: Prerequisites Check
```bash
java -version
# Should show Java 17 or higher
```

### Step 2: Download Dependencies
Create `pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.secretsharing</groupId>
    <artifactId>secret-solver</artifactId>
    <version>1.0</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
    </dependencies>
</project>
```

### Step 3: Setup Project Structure
```bash
mkdir secret-sharing-solver
cd secret-sharing-solver
mkdir -p src/main/java
mkdir -p src/test/resources
```

### Step 4: Place the Java File
Save `SecretSharingSolver.java` in `src/main/java/`

### Step 5: Install Dependencies
```bash
mvn clean compile
```

### Step 6: Create Sample Input
Create `input.json`:
```json
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {
    "base": 10,
    "value": "4"
  },
  "2": {
    "base": 2,
    "value": "111"
  },
  "3": {
    "base": 10,
    "value": "12"
  },
  "6": {
    "base": 4,
    "value": "213"
  }
}
```

### Step 7: Run the Program
```bash
java -cp "target/classes:$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)" SecretSharingSolver input.json
```

## Alternative Methods

### Method 1: Using Maven Exec Plugin

Add to `pom.xml`:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.1.0</version>
            <configuration>
                <mainClass>SecretSharingSolver</mainClass>
                <args>
                    <arg>input.json</arg>
                </args>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Run:
```bash
mvn exec:java
```

### Method 2: Create Executable JAR

Add to `pom.xml`:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.4.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>SecretSharingSolver</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Build and run:
```bash
mvn clean package
java -jar target/secret-solver-1.0.jar input.json
```

### Method 3: Direct Compilation (No Maven)

Download Jackson JARs:
```bash
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar
wget https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar
```

Compile:
```bash
javac -cp "jackson-core-2.15.2.jar:jackson-databind-2.15.2.jar:jackson-annotations-2.15.2.jar" SecretSharingSolver.java
```

Run:
```bash
java -cp ".:jackson-core-2.15.2.jar:jackson-databind-2.15.2.jar:jackson-annotations-2.15.2.jar" SecretSharingSolver input.json
```

## Input File Examples

### Example 1: Basic Shares
```json
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {"base": 10, "value": "4"},
  "2": {"base": 2, "value": "111"},
  "3": {"base": 10, "value": "12"},
  "6": {"base": 4, "value": "213"}
}
```

### Example 2: With Mathematical Functions
```json
{
  "keys": {
    "n": 6,
    "k": 4
  },
  "1": {"base": 10, "value": "multiply(4, 2)"},
  "2": {"base": 10, "value": "add(5, 2)"},
  "3": {"base": 10, "value": "gcd(12, 8)"},
  "4": {"base": 16, "value": "1A"},
  "5": {"base": 10, "value": "subtract(15, 3)"},
  "6": {"base": 10, "value": "power(2, 4)"}
}
```

### Example 3: Large Numbers
```json
{
  "keys": {
    "n": 5,
    "k": 3
  },
  "1": {"base": 10, "value": "123456789012345"},
  "2": {"base": 16, "value": "1A2B3C4D5E6F"},
  "3": {"base": 10, "value": "multiply(999999, 888888)"},
  "4": {"base": 8, "value": "777666555444"},
  "5": {"base": 10, "value": "987654321098765"}
}
```

## Troubleshooting

### Common Issues and Solutions

#### 1. "ClassNotFoundException: SecretSharingSolver"
**Problem**: Java can't find the main class
**Solution**: 
```bash
# Make sure you're in the right directory and classpath is correct
java -cp "target/classes:lib/*" SecretSharingSolver input.json
```

#### 2. "NoClassDefFoundError: com/fasterxml/jackson"
**Problem**: Jackson library not in classpath
**Solution**: 
```bash
# Download Jackson JARs or use Maven
mvn dependency:copy-dependencies
java -cp "target/classes:target/dependency/*" SecretSharingSolver input.json
```

#### 3. "FileNotFoundException: input.json"
**Problem**: Input file not found
**Solution**: 
```bash
# Use absolute path or ensure file is in current directory
java SecretSharingSolver /full/path/to/input.json
```

#### 4. "Error: main method not found"
**Problem**: Main method signature issue
**Solution**: Ensure main method is `public static void main(String[] args)`

#### 5. Memory Issues with Large Numbers
**Problem**: OutOfMemoryError
**Solution**: 
```bash
java -Xmx4g -cp "classpath" SecretSharingSolver input.json
```

## IDE Setup

### IntelliJ IDEA
1. Create new Maven project
2. Copy pom.xml content
3. Place Java file in src/main/java
4. Right-click → Run 'SecretSharingSolver.main()'
5. Edit configuration to add program arguments: `input.json`

### Eclipse
1. New → Maven Project
2. Copy pom.xml and Java file
3. Right-click project → Maven → Reload Projects
4. Run As → Java Application
5. Run Configurations → Arguments → Program arguments: `input.json`

### VS Code
1. Install Java Extension Pack
2. Open folder with pom.xml
3. Create launch.json:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "SecretSharingSolver",
            "request": "launch",
            "mainClass": "SecretSharingSolver",
            "args": ["input.json"]
        }
    ]
}
```

## Docker Setup (Optional)

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/secret-solver-1.0.jar app.jar
COPY input.json input.json

CMD ["java", "-jar", "app.jar", "input.json"]
```

### Build and Run
```bash
docker build -t secret-solver .
docker run secret-solver
```

## Batch Processing

### Process Multiple Files
```bash
#!/bin/bash
for file in inputs/*.json; do
    echo "Processing $file"
    java -cp "target/classes:target/dependency/*" SecretSharingSolver "$file"
done
```

## Performance Optimization

### JVM Tuning for Large Inputs
```bash
java -Xms2g -Xmx8g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
     -cp "classpath" SecretSharingSolver input.json
```

### Parallel Processing (Custom)
```bash
# Process multiple files in parallel
ls inputs/*.json | xargs -n 1 -P 4 -I {} java -cp "classpath" SecretSharingSolver {}
```

## Success Indicators

When running successfully, you should see:
- No compilation errors
- Clean program execution
- Numeric output (the secret)
- No Java exceptions

Example successful output:
```
123456789
```

## Next Steps

After successful execution:
1. Test with your own JSON inputs
2. Modify the code for specific requirements
3. Set up automated testing
4. Deploy to production environment