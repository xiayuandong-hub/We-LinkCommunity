package co.yiiu.welink.hook;

import org.aspectj.lang.annotation.Pointcut;

public class FileUtilHook {

    @Pointcut("execution(public * co.yiiu.welink.util.FileUtil.upload(..))")
    public void upload() {
    }

}
