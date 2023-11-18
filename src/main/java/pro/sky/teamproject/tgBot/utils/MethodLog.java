package pro.sky.teamproject.tgBot.utils;

public class MethodLog {

    public static String getMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return stackTraceElements[2].getMethodName();
    }
}