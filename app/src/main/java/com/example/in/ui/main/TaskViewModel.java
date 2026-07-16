package com.example.in.ui.main;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.in.data.dao.TaskDao;
import com.example.in.data.entity.Task;
import com.example.in.repository.TaskRepository;
import com.example.in.data.database.AppDatabase;
import java.util.List;
public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> tasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getDatabase(application);
        TaskDao taskDao = database.taskDao();

        this.repository = new TaskRepository(taskDao);
        this.tasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return tasks;
    }

    public void addTask(String detail) {
        Task task = new Task();
        task.taskDetail = detail;
        task.isCompleted = false;
        task.createdAt = System.currentTimeMillis();
        repository.insert(task);
    }

    public void updateTask(Task task) {
        repository.update(task);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }
}


