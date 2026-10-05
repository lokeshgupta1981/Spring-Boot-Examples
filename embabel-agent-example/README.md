Source code for the article https://howtodoinjava.com/?p=44111

# Embabel Agent Example with Spring Boot and Ollama

A small Embabel agent that turns a reader's message into a reading plan.
The agent has an LLM action that extracts a typed *ReadingRequest*, a plain Java action that looks up
books in a library catalog, and two goal actions gated by *@Condition* methods. The *planReading*
action gives the LLM a *calculateDailyPages* tool. A REST endpoint runs the agent.

## Versions

- Java 25
- Spring Boot 4.1.1
- Embabel Agent 1.5.2 (*com.embabel.agent:embabel-agent-starter-ollama*, built on Spring AI 2.0.1)
- Ollama with the *qwen2.5:7b* model
- Maven 3.9.x

## Run the tests

The tests use Embabel's fake and mock LLM support, so they need no API key and no running Ollama.

```bash
mvn test
```

## Run the app against Ollama

```bash
docker run -d --name ollama -p 11434:11434 ollama/ollama
docker exec ollama ollama pull qwen2.5:7b
mvn spring-boot:run
```

Call the agent:

```bash
curl -X POST localhost:8080/reading-plans -H 'Content-Type: text/plain' \
     -d 'Hi, I am Lokesh. I want to read a fantasy book in 10 days.'

curl -X POST localhost:8080/reading-plans -H 'Content-Type: text/plain' \
     -d 'Alex here. Any poetry I can finish in 7 days?'
```

On a 2-core machine without a GPU, one request took between 12 seconds and 4 minutes with *qwen2.5:7b*. The smaller *qwen2.5:3b*
model is faster, but it often answers with plain text after the tool call instead of JSON.
To try another model, change *embabel.models.default-llm* in *application.yml*.
