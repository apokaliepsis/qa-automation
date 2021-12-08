package ru.kamatech.qaaf.report.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;

import java.io.File;
import java.util.UUID;

import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.model.Status.FAILED;
import static io.qameta.allure.model.Status.PASSED;

public class AllureTuning {
    public static String startStep(String stepName){
        String uuid = UUID.randomUUID().toString();
        StepResult result = new StepResult().setName(stepName);
        getLifecycle().startStep(uuid, result);
        return uuid;
    }

    public static void setFailedStep(String uuid, String textError){
        Allure.step(textError, FAILED);
        getLifecycle().updateStep(uuid, s -> s.setStatus(FAILED));
        getLifecycle().stopStep(uuid);
    }
    public static void setPassedStep(String uuid){
        getLifecycle().updateStep(uuid, s -> s.setStatus(PASSED));
        getLifecycle().stopStep(uuid);
    }
    public static void stopSuccessStep(String uuid){
        getLifecycle().updateStep(uuid, s -> s.setStatus(PASSED));
        getLifecycle().stopStep(uuid);
    }
}
