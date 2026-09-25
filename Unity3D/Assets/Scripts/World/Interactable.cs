using System.Collections.Generic;
using UnityEngine;

namespace MasirServat
{
    public enum InteractionType
    {
        CafeJob,
        DeliveryJob,
        University,
        Bank,
        Shop,
        Gym,
        Home,
        Business,
        Vehicle,
        NPC
    }

    public sealed class Interactable : MonoBehaviour
    {
        public static readonly List<Interactable> All = new List<Interactable>();

        public InteractionType type;
        public string title;
        public string prompt;
        public long basePay = 650_000;
        public VehicleMount vehicle;

        void OnEnable()
        {
            if (!All.Contains(this)) All.Add(this);
        }

        void OnDisable() => All.Remove(this);

        public void Interact()
        {
            switch (type)
            {
                case InteractionType.CafeJob:
                    JobMinigame.I?.Begin("cafe", "شیفت کافه", basePay);
                    break;

                case InteractionType.DeliveryJob:
                    if (!GameState.I.Data.hasScooter)
                    {
                        HUDController.I?.Toast("اول باید موتور بخری");
                    }
                    else
                    {
                        JobMinigame.I?.Begin("delivery", "پیک شهری", basePay);
                    }
                    break;

                case InteractionType.University:
                    if (EconomySystem.I.Study())
                        HUDController.I?.Toast("مهارت +۴");
                    else
                        HUDController.I?.Toast("پول یا انرژی کافی نداری");
                    break;

                case InteractionType.Bank:
                    if (EconomySystem.I.Deposit(2_000_000))
                        HUDController.I?.Toast("۲ میلیون به صندوق امن رفت");
                    else
                        HUDController.I?.Toast("برای این واریز پول کافی نداری");
                    break;

                case InteractionType.Shop:
                    if (EconomySystem.I.BuyLaptop())
                        HUDController.I?.Toast("لپ‌تاپ ارتقا پیدا کرد");
                    else
                        HUDController.I?.Toast("یا پول کافی نداری، یا لپ‌تاپت Max شده");
                    break;

                case InteractionType.Gym:
                    if (EconomySystem.I.Gym())
                        HUDController.I?.Toast("تمرین کردی · اعتبار +۲");
                    else
                        HUDController.I?.Toast("پول یا انرژی کافی نداری");
                    break;

                case InteractionType.Home:
                    EconomySystem.I.SleepAndAdvanceDay();
                    break;

                case InteractionType.Business:
                    if (EconomySystem.I.UpgradeBusiness())
                        HUDController.I?.Toast("کسب‌وکارت رشد کرد");
                    else
                        HUDController.I?.Toast("برای این ارتقا پول کافی نداری");
                    break;

                case InteractionType.Vehicle:
                    vehicle?.Use();
                    break;

                case InteractionType.NPC:
                    var d = GameState.I.Data;
                    d.reputation = Mathf.Min(100, d.reputation + 1);
                    GameState.I.NotifyChanged();
                    HUDController.I?.Toast("یک گفت‌وگوی کوتاه · اعتبار +۱");
                    break;
            }

            HUDController.I?.Refresh();
        }
    }
}
