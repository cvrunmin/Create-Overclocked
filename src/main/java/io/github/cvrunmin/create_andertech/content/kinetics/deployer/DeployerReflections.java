package io.github.cvrunmin.create_andertech.content.kinetics.deployer;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import io.github.cvrunmin.create_andertech.CreateAndertech;

import java.lang.reflect.Field;

public class DeployerReflections {
    private static Class<? extends Enum> stateClazz;
    private static Class<? extends Enum> modeClazz;

    private static Enum WAITING, EXPANDING, RETRACTING, DUMPING;
    private static Enum PUNCH, USE;

    private static boolean shouldInitialize(){
        return stateClazz == null || modeClazz == null || WAITING == null || EXPANDING == null || RETRACTING == null || DUMPING == null || PUNCH == null || USE == null;
    }

    private static void initializeReflection(){
        Field stateField;
        Field moField;
        Class<? extends Enum> stateEnum;
        Class<? extends Enum> modeEnum;

        try {
            stateField = DeployerBlockEntity.class.getDeclaredField("state");
            moField = DeployerBlockEntity.class.getDeclaredField("mode");
            stateField.setAccessible(true);
            moField.setAccessible(true);
            stateEnum = (Class<? extends Enum>) stateField.getType();
            modeEnum = (Class<? extends Enum>) moField.getType();
            stateClazz = stateEnum;
            modeClazz = modeEnum;
            Enum[] stateEnumConsts = stateEnum.getEnumConstants();
            Enum[] enumConstants = modeEnum.getEnumConstants();
            for (Enum stateEnumConst : stateEnumConsts) {
                switch (stateEnumConst.name()) {
                    case "WAITING" -> WAITING = stateEnumConst;
                    case "EXPANDING" -> EXPANDING = stateEnumConst;
                    case "RETRACTING" -> RETRACTING = stateEnumConst;
                    case "DUMPING" -> DUMPING = stateEnumConst;
                }
            }
            for (Enum object : enumConstants) {
                String enumName = object.name();
                if(enumName.equalsIgnoreCase("punch")){
                    PUNCH = object;
                }
                else if(enumName.equalsIgnoreCase("use")){
                    USE = object;
                }
            }
        } catch (NoSuchFieldException | SecurityException e) {
            CreateAndertech.LOGGER.warn("cannot retrieve reflection information of DeployerBlockEntity.", e);
        }
    }

    public static Enum getUSE() {
        if(shouldInitialize())
            initializeReflection();
        return USE;
    }

    public static Enum getPUNCH() {
        if(shouldInitialize())
            initializeReflection();
        return PUNCH;
    }

    public static Enum getDUMPING() {
        if(shouldInitialize())
            initializeReflection();
        return DUMPING;
    }

    public static Enum getRETRACTING() {
        if(shouldInitialize())
            initializeReflection();
        return RETRACTING;
    }

    public static Enum getEXPANDING() {
        if(shouldInitialize())
            initializeReflection();
        return EXPANDING;
    }

    public static Enum getWAITING() {
        if(shouldInitialize())
            initializeReflection();
        return WAITING;
    }

    public static Class<? extends Enum> getModeClazz() {
        return modeClazz;
    }

    public static Class<? extends Enum> getStateClazz() {
        return stateClazz;
    }
}
