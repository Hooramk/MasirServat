using UnityEngine;

namespace MasirServat
{
    public sealed class EconomySystem : MonoBehaviour
    {
        public static EconomySystem I { get; private set; }

        void Awake() => I = this;

        public bool CanSpend(long amount, int energy = 0)
        {
            var d = GameState.I.Data;
            return d.cash >= amount && d.energy >= energy;
        }

        public bool TrySpend(long amount, int energy = 0)
        {
            var d = GameState.I.Data;
            if (d.cash < amount || d.energy < energy)
                return false;

            d.cash -= amount;
            d.energy -= energy;
            GameState.I.NotifyChanged();
            return true;
        }

        public void Earn(long amount, int xp = 0)
        {
            var d = GameState.I.Data;
            d.cash += Mathf.Max(0, (int)Mathf.Min(amount, int.MaxValue));
            if (xp > 0)
            {
                d.jobXp += xp;
                while (d.jobXp >= 100)
                {
                    d.jobXp -= 100;
                    d.careerLevel++;
                    d.reputation = Mathf.Min(100, d.reputation + 3);
                }
            }
            GameState.I.NotifyChanged();
        }

        public bool Deposit(long amount)
        {
            var d = GameState.I.Data;
            if (d.cash < amount) return false;
            d.cash -= amount;
            d.savings += amount;
            GameState.I.NotifyChanged();
            QuestSystem.I?.Notify("deposit");
            return true;
        }

        public bool Study()
        {
            const long cost = 350_000;
            if (!TrySpend(cost, 1)) return false;

            var d = GameState.I.Data;
            d.skill = Mathf.Min(100, d.skill + 4);
            d.reputation = Mathf.Min(100, d.reputation + 1);
            GameState.I.NotifyChanged();
            QuestSystem.I?.Notify("study");
            return true;
        }

        public bool Gym()
        {
            const long cost = 220_000;
            if (!TrySpend(cost, 1)) return false;

            var d = GameState.I.Data;
            d.reputation = Mathf.Min(100, d.reputation + 2);
            GameState.I.NotifyChanged();
            return true;
        }

        public bool BuyLaptop()
        {
            var d = GameState.I.Data;
            long price = d.laptopLevel == 0 ? 8_000_000 : 18_000_000;
            if (d.laptopLevel >= 2 || !TrySpend(price)) return false;
            d.laptopLevel++;
            d.skill = Mathf.Min(100, d.skill + 3);
            GameState.I.NotifyChanged();
            return true;
        }

        public bool BuyScooter()
        {
            var d = GameState.I.Data;
            if (d.hasScooter || !TrySpend(9_000_000)) return false;
            d.hasScooter = true;
            d.transportLevel = 1;
            d.maxEnergy = 6;
            d.energy = Mathf.Min(d.maxEnergy, d.energy + 1);
            GameState.I.NotifyChanged();
            QuestSystem.I?.Notify("scooter");
            return true;
        }

        public bool UpgradeBusiness()
        {
            var d = GameState.I.Data;
            long price = d.businessLevel == 0 ? 20_000_000 : d.businessLevel == 1 ? 38_000_000 : 70_000_000;
            if (d.businessLevel >= 3 || !TrySpend(price)) return false;
            d.businessLevel++;
            d.reputation = Mathf.Min(100, d.reputation + 8);
            GameState.I.NotifyChanged();
            QuestSystem.I?.Notify("business");
            return true;
        }

        public void SleepAndAdvanceDay()
        {
            var d = GameState.I.Data;

            long livingCost = 210_000 + (d.roomLevel - 1) * 60_000;
            long passiveBusiness = d.businessLevel > 0
                ? 250_000L * d.businessLevel + d.reputation * 4_000L
                : 0;

            d.cash += passiveBusiness;
            d.cash -= livingCost;

            if (d.cash < 0)
            {
                d.debt += -d.cash;
                d.cash = 0;
            }

            d.day++;
            d.energy = d.maxEnergy;
            GameState.I.NotifyChanged();
            HUDController.I?.Toast(
                "روز " + d.day + " شروع شد\nهزینه زندگی: " + FormatMoney(livingCost) +
                (passiveBusiness > 0 ? "\nدرآمد کسب‌وکار: +" + FormatMoney(passiveBusiness) : "")
            );
        }

        public static string FormatMoney(long value)
        {
            if (System.Math.Abs(value) >= 1_000_000_000)
                return (value / 1_000_000_000f).ToString("0.#") + " میلیارد";
            if (System.Math.Abs(value) >= 1_000_000)
                return (value / 1_000_000f).ToString("0.#") + " م";
            if (System.Math.Abs(value) >= 1_000)
                return (value / 1_000f).ToString("0.#") + " هز";
            return value.ToString();
        }
    }
}
