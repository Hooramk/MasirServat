package ir.masirservat.app;

import android.content.SharedPreferences;

public class GameState {
    public long cash = 60_000_000L;
    public long salary = 35_000_000L;
    public long sideIncome = 0L;
    public long passiveIncome = 0L;
    public long housing = 12_000_000L;
    public long food = 7_000_000L;
    public long transport = 3_000_000L;
    public long utilities = 1_000_000L;
    public long health = 1_000_000L;
    public long discretionary = 6_000_000L;
    public long debt = 0L;
    public long debtPayment = 0L;
    public int debtMonthsLeft = 0;
    public long emergencyFund = 30_000_000L;
    public long gold = 0L;
    public long funds = 0L;
    public long business = 0L;
    public double headlineIndex = 1.0;
    public int month = 1;
    public boolean choiceMade = false;

    public long monthlyIncome() { return salary + sideIncome + passiveIncome; }
    public long monthlyExpenses() { return housing + food + transport + utilities + health + discretionary + debtPayment; }
    public long essentialExpenses() { return housing + food + transport + utilities + health + debtPayment; }
    public long netWorth() { return cash + emergencyFund + gold + funds + business - debt; }
    public long realNetWorth() { return Math.round(netWorth() / headlineIndex); }
    public long cashFlow() { return monthlyIncome() - monthlyExpenses(); }
    public double purchasingPower() { return 100.0 / headlineIndex; }

    public void settleMonth() {
        cash += monthlyIncome() - monthlyExpenses();
        if (debtMonthsLeft > 0) {
            debt = Math.max(0, debt - debtPayment);
            debtMonthsLeft--;
            if (debtMonthsLeft == 0 || debt == 0) debtPayment = 0;
        }
        applyInflation();
        month++;
        choiceMade = false;
    }

    private void applyInflation() {
        double wave = Math.sin(month * 1.73) * 0.007;
        double headline = clamp(0.034 + wave, 0.015, 0.065);
        double foodRate = clamp(headline * 1.45 + Math.cos(month * 0.91) * 0.006, 0.015, 0.11);
        double housingRate = clamp(headline * 0.70 + Math.sin(month * 0.47) * 0.004, 0.008, 0.055);
        double serviceRate = clamp(headline * 0.90, 0.012, 0.07);
        double transportRate = clamp(headline * 0.85, 0.01, 0.075);

        headlineIndex *= (1.0 + headline);
        food = Math.round(food * (1.0 + foodRate));
        housing = Math.round(housing * (1.0 + housingRate));
        utilities = Math.round(utilities * (1.0 + serviceRate));
        health = Math.round(health * (1.0 + serviceRate));
        discretionary = Math.round(discretionary * (1.0 + serviceRate));
        transport = Math.round(transport * (1.0 + transportRate));
    }

    private static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(max, x));
    }

    public void save(SharedPreferences p) {
        p.edit()
                .putLong("cash", cash).putLong("salary", salary).putLong("sideIncome", sideIncome)
                .putLong("passiveIncome", passiveIncome).putLong("housing", housing).putLong("food", food)
                .putLong("transport", transport).putLong("utilities", utilities).putLong("health", health)
                .putLong("discretionary", discretionary).putLong("debt", debt).putLong("debtPayment", debtPayment)
                .putInt("debtMonthsLeft", debtMonthsLeft).putLong("emergencyFund", emergencyFund)
                .putLong("gold", gold).putLong("funds", funds).putLong("business", business)
                .putLong("headlineIndexBits", Double.doubleToRawLongBits(headlineIndex)).putInt("month", month)
                .putBoolean("choiceMade", choiceMade).apply();
    }

    public static GameState load(SharedPreferences p) {
        GameState s = new GameState();
        if (!p.contains("month")) return s;
        s.cash=p.getLong("cash",s.cash); s.salary=p.getLong("salary",s.salary); s.sideIncome=p.getLong("sideIncome",0);
        s.passiveIncome=p.getLong("passiveIncome",0); s.housing=p.getLong("housing",s.housing); s.food=p.getLong("food",s.food);
        s.transport=p.getLong("transport",s.transport); s.utilities=p.getLong("utilities",s.utilities); s.health=p.getLong("health",s.health);
        s.discretionary=p.getLong("discretionary",s.discretionary); s.debt=p.getLong("debt",0); s.debtPayment=p.getLong("debtPayment",0);
        s.debtMonthsLeft=p.getInt("debtMonthsLeft",0); s.emergencyFund=p.getLong("emergencyFund",s.emergencyFund);
        s.gold=p.getLong("gold",0); s.funds=p.getLong("funds",0); s.business=p.getLong("business",0);
        s.headlineIndex=Double.longBitsToDouble(p.getLong("headlineIndexBits",Double.doubleToRawLongBits(1.0)));
        s.month=p.getInt("month",1); s.choiceMade=p.getBoolean("choiceMade",false);
        return s;
    }
}
