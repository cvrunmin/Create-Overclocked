package io.github.cvrunmin.create_andertech.foundation;

import io.github.cvrunmin.create_andertech.CreateAndertech;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.lang.LangBuilder;

public class MyLang extends Lang {
    public static LangBuilder builder(){
        return new LangBuilder(CreateAndertech.MODID);
    }
}
