package dev.flxwdns.multistom.task.invoker;

import dev.flxwdns.multistom.task.MultiStomTask;
import dev.flxwdns.multistom.task.annotation.MultiStomTaskEnvironment;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;

@UtilityClass
public final class MultiStomTaskInvoker {

    public static MultiStomTask invoke(Path jarPath) {
        try {
            var jarFile = new JarFile(jarPath.toString());
            var entries = jarFile.entries();

            var urls = new URL[]{jarPath.toFile().toURI().toURL()};
            var classLoader = URLClassLoader.newInstance(urls);

            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                var entryName = entry.getName();

                if (entryName.equals("task.json")) {
                    try (var inputStream = jarFile.getInputStream(entry)) {
                        // Lies die Datei in einen String ein
                        String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.out.println("Inhalt von task.json: " + taskJson);
                    }
                }

                if (entryName.endsWith(".class")) {
                    var className = entryName.replace("/", ".").replace(".class", "");

                    try {
                        Class<?> clazz = classLoader.loadClass(className);

                        String mainClass = null;
                        try (var in = clazz.getResourceAsStream("/task.json")) {
                            if(in == null) {
                                throw new RuntimeException("Resource not found: task.json");
                            }
                            try (var reader = new BufferedReader(new InputStreamReader(in))) {

                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if(line.contains("\"main\": \"")) {
                                        mainClass = line.split(" ")[3].replace("\"", "").replace(" ", "");
                                    }
                                }
                            }
                        }
                        if(mainClass == null) {
                            throw new RuntimeException("Main class not found in task.json. Please check your task.json file.");
                        }

                        if (clazz.getName().equals(mainClass)) {
                            if (clazz.newInstance() instanceof MultiStomTask task) {
                                if (Arrays.stream(task.getClass().getAnnotations()).noneMatch(it -> it instanceof MultiStomTaskEnvironment)) {
                                    throw new RuntimeException("Task does not have MultiStomTaskEnvironment annotation. " + jarPath);
                                }
                                var environment = task.getClass().getAnnotation(MultiStomTaskEnvironment.class);
                                task.environment(environment);

                                return task;
                            } else {
                                throw new RuntimeException("Task does not extend MultiStomTask. " + jarPath);
                            }
                        } else {
                            throw new RuntimeException("Provided main-class is not existing. " + mainClass);
                        }
                    } catch (ClassNotFoundException exception) {
                        throw new RuntimeException("Class not found: " + className);
                    }
                }
            }

            jarFile.close();
        } catch (IOException | ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
        throw new RuntimeException("Task not found in jar: " + jarPath);
    }
}
