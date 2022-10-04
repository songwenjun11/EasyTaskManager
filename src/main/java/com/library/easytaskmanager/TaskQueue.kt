package com.library.easytaskmanager

class TaskQueue private constructor() {
    private val tasks = mutableListOf<Task>()

    @Volatile
    var cursor = -1//游标指针

    companion object {
        @JvmStatic
        @Volatile
        private var taskQueue: TaskQueue? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): TaskQueue {
            if (taskQueue == null) {
                synchronized(TaskQueue::class.java) {
                    if (taskQueue == null) {
                        taskQueue = TaskQueue()
                    }
                }
            }
            return taskQueue!!
        }
    }

    /**
     * 添加新任务
     */
    fun push(task: Task) {
        when (task.priority) {
            Priority.DEFAULT -> {
                //默认排序
                tasks.add(task)
                task.inserted()
            }
            Priority.NEXT -> {
                //下一个
                if (tasks.isNotEmpty()) {
                    if (tasks.size >= 2) {
                        tasks.add(1, task)
                    } else {
                        tasks.add(task)
                    }
                } else {
                    tasks.add(task)
                }
                task.inserted()
            }
            Priority.PROMPTLY -> {
                //立刻执行
                if (tasks.size > 0) {
                    tasks[0].onSuspend()
                }
                tasks.add(0, task)
                task.inserted()
            }
        }
        notifyAllAwaitNum()
    }

    /**
     * 删除任务
     */
    fun pop(task: Task) {
        task.onSuspend()
        task.onFinish()
        tasks.remove(task)
        notifyAllAwaitNum()
    }

    /**
     * 获取下一个任务
     */
    fun next(): Task? {
        if (cursor + 1 >= tasks.size) {
            cursor = -1
        }
        if (tasks.isEmpty()) {
            return null
        }
        if (tasks.size <= 1) {
            cursor = -1
        }
        cursor++
        return tasks[cursor]
    }

    /**
     * 通知除了正在执行的任务除外的所有任务更新排队数量
     */
    private fun notifyAllAwaitNum() {
        for (index in 1 until tasks.size) {
            tasks[index].onAwait(index + 1)
        }
    }
}