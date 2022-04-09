package com.camarederic.yourtasks.adapter;

import com.camarederic.yourtasks.model.Task;

public interface OnTodoClickListener {

    void onTodoClick(Task task);

    void onTodoRadioButtonClick(Task task);
}
