package exam_project.artigiani_webapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * Salva il file nel file system locale e ritorna l'URL relativo.
     *
     * Formati supportati:
     *  - .glb / .gltf singolo  → salvato direttamente
     *  - .zip contenente .gltf + .bin + texture → estratto in una sottocartella
     *    e ritorna l'URL del file .gltf principale
     *  - immagini (.jpg, .png, ecc.) → salvate direttamente
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")).toLowerCase()
                : "";

        // ZIP: potrebbe contenere un pacchetto GLTF multi-file
        if (".zip".equals(extension)) {
            return extractZipAndFindGltf(file.getInputStream(), uploadPath);
        }

        // File singolo (.glb, .gltf, immagine, ecc.)
        String fileName = UUID.randomUUID() + extension;
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        return "/uploads/" + fileName;
    }

    /**
     * Estrae uno ZIP in una sottocartella dedicata (UUID) e ritorna
     * l'URL del primo file .gltf trovato al suo interno.
     * Se non contiene .gltf, ritorna l'URL del primo file estratto.
     */
    private String extractZipAndFindGltf(InputStream zipStream, Path uploadPath) throws IOException {
        String folderName = UUID.randomUUID().toString();
        Path destDir = uploadPath.resolve(folderName);
        Files.createDirectories(destDir);

        String gltfRelPath = null;
        String firstRelPath = null;

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = Paths.get(entry.getName()).getFileName().toString(); // no path traversal
                if (entry.isDirectory() || entryName.isBlank()) {
                    zis.closeEntry();
                    continue;
                }
                Path outPath = destDir.resolve(entryName);
                Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);

                String rel = "/uploads/" + folderName + "/" + entryName;
                if (firstRelPath == null) firstRelPath = rel;
                if (entryName.toLowerCase().endsWith(".gltf") && gltfRelPath == null) {
                    gltfRelPath = rel;
                }
                zis.closeEntry();
            }
        }

        return gltfRelPath != null ? gltfRelPath : firstRelPath;
    }
}
