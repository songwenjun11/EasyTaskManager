package com.library.easytaskmanager

import android.app.Application
import java.util.*

/**
 * 任务统一管理
 */
class TaskManager private constructor() : Thread() {
    /**
     * 创建任务列表
     */
    private val taskQueue: TaskQueue = TaskQueue.getInstance()

    companion object {
        @JvmStatic
        @Volatile
        private var taskManager: TaskManager? = null

        @JvmStatic
        private var application: Application? = null

        @JvmStatic
        @Synchronized
        fun getInstance(): TaskManager {
            if (taskManager == null) {
                synchronized(TaskManager::class.java) {
                    if (taskManager == null) {
                        taskManager = TaskManager()
                    }
                }
            }
            return taskManager!!
        }

        @JvmStatic
        fun init(application: Application) {
            if (this.application == null) {
                this.application = application
            } else {
                throw Exception("请不要重复初始化TaskManager")
            }
        }
    }

    init {
        start()
    }

    override fun run() {
        super.run()
        while (true) {
            //死循环等待新任务插入
            val next = taskQueue.next()
            next?.let {
                it.onStart()//TODO 如果存在线程会有问题 待完善
                it.onFinish()
            }
        }
    }

    /**
     * 插入任务
     */
    fun insertTask(task: Task) {
        taskQueue.push(task)
    }
}