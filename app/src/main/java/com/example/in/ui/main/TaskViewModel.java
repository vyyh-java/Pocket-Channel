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

// 1. 必须继承自 AndroidViewModel（而不是 ViewModel）
public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getDatabase(application);
        TaskDao taskDao = database.taskDao();

        this.repository = new TaskRepository(taskDao);
        this.allTasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void addTask(String detail) {
        Task task = new Task();
        task.taskDetail = detail;
        task.isCompleted = false;
        repository.insert(task);
    }

    public void updateTask(Task task) {
        repository.update(task);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }
}


