package top.leonam.hotbctgamess.service;

import top.leonam.hotbctgamess.model.entity.Crime;

import java.util.Random;

public class CrimeUtils {
    private static final Random random = new Random();

    public static double randomValueCrime(Crime crime){
        return random.nextDouble(crime.getMinReward().intValue(), crime.getMaxReward().intValue());
    }
    public static boolean successCrime(Crime crime){
        return random.nextInt(0, 100) < crime.getSuccessChance();
    }
    public static boolean policeArrested(Crime crime){
        return random.nextInt(0, 100) < crime.getPoliceRisk();
    }

}
