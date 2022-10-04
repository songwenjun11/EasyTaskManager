package com.library.easytaskmanager

abstract class Task(val priority: Priority = Priority.DEFAULT) {
    /**
     * 已插入任务列表
     * 添加到任务列表时
     */
    open fun inserted() {}

    /**
     * 任务等待执行
     * 在排队状态中，未执行
     *
     * @param currentNum 当前第几名
     */
    open fun onAwait(currentNum: Int) {}

    /**
     * 开始任务
     * 开始执行当前任务时
     */
    open fun onStart() {}

    /**
     * 挂起任务
     * 当前正在执行时，有任务插队 会将该任务进行挂起
     */
    open fun onSuspend() {}

    /**
     * 任务结束
     * 当前任务结束时执行
     */
    open fun onFinish() {}

    /**
     * 关闭任务
     */
    fun close() {

    }

    /**
     * 重启任务
     */
    fun reStart() {

    }
}