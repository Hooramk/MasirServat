using UnityEngine;

namespace MasirServat
{
    public sealed class QuestSystem : MonoBehaviour
    {
        public static QuestSystem I { get; private set; }

        readonly string[] titles =
        {
            "اولین شیفتت را در کافه کامل کن",
            "در دانشگاه یک مهارت تمرین کن",
            "۲ میلیون تومان در بانک پس‌انداز کن",
            "اولین موتور خودت را بخر",
            "ملک خالی را به کسب‌وکار تبدیل کن",
            "حالا شهر برای تو باز است؛ ثروتت را بساز"
        };

        void Awake() => I = this;

        public string CurrentTitle
        {
            get
            {
                int step = Mathf.Clamp(GameState.I.Data.questStep, 0, titles.Length - 1);
                return titles[step];
            }
        }

        public void Notify(string key)
        {
            var d = GameState.I.Data;
            int before = d.questStep;

            if (d.questStep == 0 && key == "cafe_job") d.questStep = 1;
            else if (d.questStep == 1 && key == "study") d.questStep = 2;
            else if (d.questStep == 2 && key == "deposit" && d.savings >= 2_000_000) d.questStep = 3;
            else if (d.questStep == 3 && key == "scooter") d.questStep = 4;
            else if (d.questStep == 4 && key == "business") d.questStep = 5;

            if (d.questStep != before)
            {
                d.cash += 500_000;
                d.reputation = Mathf.Min(100, d.reputation + 2);
                GameState.I.NotifyChanged();
                HUDController.I?.Toast("ماموریت کامل شد!  +۵۰۰هزار تومان");
            }

            HUDController.I?.Refresh();
        }
    }
}
