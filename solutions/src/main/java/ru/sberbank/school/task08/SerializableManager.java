package ru.sberbank.school.task08;

import lombok.NonNull;
import ru.sberbank.school.task08.state.*;
import ru.sberbank.school.util.Solution;

import java.io.*;
import java.util.List;

@Solution(8)
public class SerializableManager extends SaveGameManager {
    private String separator = File.separator;

    public SerializableManager(@NonNull String filesDirectoryPath) {
        super(filesDirectoryPath);
    }

    @Override
    public void initialize() {
        if (filesDirectory.equals("")) {
            separator = "";
        }
    }

    @Override
    public void saveGame(@NonNull String filename,
                         @NonNull Savable gameState) throws SaveGameException {
        try (FileOutputStream fos = new FileOutputStream(filesDirectory + separator + filename);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameState);
            oos.flush();
        } catch (FileNotFoundException e) {
            throw new SaveGameException("Открыть файл не удалось.", e,
                    SaveGameException.Type.IO, gameState);
        } catch (IOException e) {
            throw new SaveGameException("Ошибка записи в файл!", e,
                    SaveGameException.Type.IO, gameState);
        }
    }

    @Override
    public Savable loadGame(@NonNull String filename) throws SaveGameException {
        Savable savable = null;
        try (FileInputStream fis = new FileInputStream(filesDirectory + separator + filename);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            savable = (Savable) ois.readObject();
        } catch (IOException e) {
            throw new SaveGameException("Ошибка чтения!", e,
                    SaveGameException.Type.IO, savable);
        } catch (ClassNotFoundException e) {
            throw new SaveGameException("Определение класса не найдено!", e,
                    SaveGameException.Type.USER, savable);
        }

        return savable;
    }

    @Override
    public InstantiatableEntity createEntity(InstantiatableEntity.Type type,
                                             InstantiatableEntity.Status status,
                                             long hitPoints) {
        return new GameObject(type, status, hitPoints);
    }

    @Override
    public Savable createSavable(String name, List entities) {
        return new MapState<>(name, entities);
    }

}
