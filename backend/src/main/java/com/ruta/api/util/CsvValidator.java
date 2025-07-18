
package com.ruta.api.util;

import com.ruta.api.model.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class CsvValidator {
    public static List<Connection> parseCSV(MultipartFile file) throws IOException {
        List<Connection> Connections = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length != 3) continue;
            Connections.add(new Connection(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
        return Connections;
    }
}
