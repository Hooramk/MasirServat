using System.Collections;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.UI;

namespace MasirServat
{
    public sealed class HUDController : MonoBehaviour
    {
        public static HUDController I { get; private set; }

        Canvas canvas;
        Font font;
        Text moneyText, energyText, skillText, reputationText, missionText, promptText, toastText, guideText;
        Button interactButton;
        Coroutine toastRoutine;

        public Transform CanvasTransform => canvas != null ? canvas.transform : null;

        void Awake()
        {
            I = this;
            font = UIFontProvider.Get();
            Build();
        }

        void OnEnable()
        {
            if (GameState.I != null) GameState.I.Changed += Refresh;
        }

        void OnDisable()
        {
            if (GameState.I != null) GameState.I.Changed -= Refresh;
        }

        void Start() => Refresh();

        void Build()
        {
            var canvasGo = new GameObject("HUD");
            canvasGo.transform.SetParent(transform, false);
            canvas = canvasGo.AddComponent<Canvas>();
            canvas.renderMode = RenderMode.ScreenSpaceOverlay;
            canvas.sortingOrder = 20;

            var scaler = canvasGo.AddComponent<CanvasScaler>();
            scaler.uiScaleMode = CanvasScaler.ScaleMode.ScaleWithScreenSize;
            scaler.referenceResolution = new Vector2(1920, 1080);
            scaler.matchWidthOrHeight = 0.5f;
            canvasGo.AddComponent<GraphicRaycaster>();

            var top = Panel("TopBar", canvas.transform, new Color(0.03f, 0.08f, 0.14f, 0.94f));
            SetRect(top.rectTransform, new Vector2(0, 1), new Vector2(1, 1), Vector2.zero, new Vector2(0, 112), new Vector2(0.5f, 1));

            moneyText = Label("Money", top.transform, "", 30, TextAnchor.MiddleCenter);
            SetRect(moneyText.rectTransform, new Vector2(0.00f, 0), new Vector2(0.22f, 1), Vector2.zero, Vector2.zero);

            energyText = Label("Energy", top.transform, "", 30, TextAnchor.MiddleCenter);
            SetRect(energyText.rectTransform, new Vector2(0.22f, 0), new Vector2(0.42f, 1), Vector2.zero, Vector2.zero);

            skillText = Label("Skill", top.transform, "", 30, TextAnchor.MiddleCenter);
            SetRect(skillText.rectTransform, new Vector2(0.42f, 0), new Vector2(0.62f, 1), Vector2.zero, Vector2.zero);

            reputationText = Label("Reputation", top.transform, "", 30, TextAnchor.MiddleCenter);
            SetRect(reputationText.rectTransform, new Vector2(0.62f, 0), new Vector2(0.82f, 1), Vector2.zero, Vector2.zero);

            var day = Label("Day", top.transform, "", 28, TextAnchor.MiddleCenter);
            SetRect(day.rectTransform, new Vector2(0.82f, 0), new Vector2(1, 1), Vector2.zero, Vector2.zero);
            day.name = "DayText";

            var missionPanel = Panel("MissionPanel", canvas.transform, new Color(0.02f, 0.06f, 0.11f, 0.84f));
            SetRect(missionPanel.rectTransform, new Vector2(0.02f, 1), new Vector2(0.55f, 1), new Vector2(0, -138), new Vector2(0, 62), new Vector2(0, 1));
            missionText = Label("Mission", missionPanel.transform, "", 26, TextAnchor.MiddleRight);
            missionText.color = new Color(0.95f, 0.84f, 0.48f);
            SetRect(missionText.rectTransform, Vector2.zero, Vector2.one, new Vector2(14, 0), new Vector2(-14, 0));

            guideText = Label("Guide", canvas.transform, "", 24, TextAnchor.MiddleCenter);
            guideText.color = new Color(0.96f, 0.93f, 0.82f);
            guideText.gameObject.AddComponent<Outline>().effectColor = new Color(0, 0, 0, 0.78f);
            SetRect(guideText.rectTransform, new Vector2(0.22f, 0), new Vector2(0.78f, 0), new Vector2(0, 250), new Vector2(0, 76), new Vector2(0.5f, 0));

            promptText = Label("Prompt", canvas.transform, "", 27, TextAnchor.MiddleCenter);
            promptText.color = Color.white;
            promptText.gameObject.AddComponent<Outline>().effectColor = new Color(0, 0, 0, 0.65f);
            SetRect(promptText.rectTransform, new Vector2(0.28f, 0), new Vector2(0.72f, 0), new Vector2(0, 170), new Vector2(0, 56), new Vector2(0.5f, 0));

            toastText = Label("Toast", canvas.transform, "", 28, TextAnchor.MiddleCenter);
            toastText.color = Color.white;
            var toastBg = toastText.gameObject.AddComponent<Outline>();
            toastBg.effectColor = new Color(0, 0, 0, 0.8f);
            SetRect(toastText.rectTransform, new Vector2(0.28f, 0.5f), new Vector2(0.72f, 0.5f), Vector2.zero, new Vector2(0, 150), new Vector2(0.5f, 0.5f));

            BuildMovementPad(canvas.transform);

            interactButton = Button("Interact", canvas.transform, "تعامل", new Color(0.78f, 0.64f, 0.36f));
            SetRect(interactButton.GetComponent<RectTransform>(), new Vector2(1, 0), new Vector2(1, 0), new Vector2(-145, 135), new Vector2(220, 120), new Vector2(0.5f, 0.5f));
            interactButton.onClick.AddListener(() => WorldInteraction.I?.Interact());
        }

        void BuildMovementPad(Transform parent)
        {
            var baseGo = new GameObject("VirtualJoystick", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            baseGo.transform.SetParent(parent, false);
            var baseRt = baseGo.GetComponent<RectTransform>();
            baseRt.anchorMin = baseRt.anchorMax = new Vector2(0, 0);
            baseRt.pivot = new Vector2(0.5f, 0.5f);
            baseRt.anchoredPosition = new Vector2(175, 165);
            baseRt.sizeDelta = new Vector2(230, 230);

            var baseImage = baseGo.GetComponent<Image>();
            baseImage.color = new Color(0.03f, 0.08f, 0.14f, 0.58f);

            var knobGo = new GameObject("Knob", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            knobGo.transform.SetParent(baseGo.transform, false);
            var knobRt = knobGo.GetComponent<RectTransform>();
            knobRt.anchorMin = knobRt.anchorMax = new Vector2(0.5f, 0.5f);
            knobRt.pivot = new Vector2(0.5f, 0.5f);
            knobRt.anchoredPosition = Vector2.zero;
            knobRt.sizeDelta = new Vector2(94, 94);
            knobGo.GetComponent<Image>().color = new Color(0.88f, 0.90f, 0.92f, 0.82f);

            var joystick = baseGo.AddComponent<VirtualJoystick>();
            joystick.knob = knobRt;
            joystick.radius = 82f;
        }

        public void Refresh()
        {
            if (GameState.I == null) return;
            var d = GameState.I.Data;
            SetText(moneyText, "💰 " + EconomySystem.FormatMoney(d.cash));
            SetText(energyText, "⚡ " + d.energy + "/" + d.maxEnergy);
            SetText(skillText, "🧠 " + d.skill);
            SetText(reputationText, "⭐ " + d.reputation);

            var dayText = canvas.transform.Find("TopBar/DayText")?.GetComponent<Text>();
            if (dayText != null) SetText(dayText, "روز " + d.day);

            if (missionText != null && QuestSystem.I != null)
                SetText(missionText, "ماموریت: " + QuestSystem.I.CurrentTitle);

            if (guideText != null)
                SetText(guideText, GuideForStep(d.questStep));
        }

        public void SetPrompt(string text)
        {
            if (promptText == null) return;
            SetText(promptText, string.IsNullOrEmpty(text) ? "" : "◉ " + text);

            if (interactButton != null)
            {
                interactButton.interactable = true;
                var label = interactButton.transform.Find("Label")?.GetComponent<Text>();
                if (label != null)
                    SetText(label, string.IsNullOrEmpty(text) ? "نزدیک‌تر شو" : "تعامل");
            }
        }

        public void Toast(string message)
        {
            if (toastText == null) return;
            if (toastRoutine != null) StopCoroutine(toastRoutine);
            toastRoutine = StartCoroutine(ToastRoutine(message));
        }

        IEnumerator ToastRoutine(string message)
        {
            SetText(toastText, message);
            toastText.canvasRenderer.SetAlpha(1f);
            yield return new WaitForSeconds(2.4f);
            toastText.CrossFadeAlpha(0f, 0.35f, true);
            yield return new WaitForSeconds(0.4f);
            toastText.text = "";
            toastText.canvasRenderer.SetAlpha(1f);
        }

        public Canvas GetCanvas() => canvas;

        Image Panel(string name, Transform parent, Color color)
        {
            var go = new GameObject(name, typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            go.transform.SetParent(parent, false);
            var image = go.GetComponent<Image>();
            image.color = color;
            return image;
        }

        Text Label(string name, Transform parent, string text, int size, TextAnchor anchor)
        {
            var go = new GameObject(name, typeof(RectTransform), typeof(CanvasRenderer), typeof(Text));
            go.transform.SetParent(parent, false);
            var t = go.GetComponent<Text>();
            t.font = font;
            t.text = PersianText.Fix(text);
            t.fontSize = size;
            t.alignment = anchor;
            t.color = Color.white;
            t.resizeTextForBestFit = true;
            t.resizeTextMinSize = 18;
            t.resizeTextMaxSize = size;
            t.horizontalOverflow = HorizontalWrapMode.Wrap;
            t.verticalOverflow = VerticalWrapMode.Truncate;
            return t;
        }

        Button Button(string name, Transform parent, string text, Color color)
        {
            var go = new GameObject(name, typeof(RectTransform), typeof(CanvasRenderer), typeof(Image), typeof(Button));
            go.transform.SetParent(parent, false);
            var image = go.GetComponent<Image>();
            image.color = color;
            var button = go.GetComponent<Button>();
            button.targetGraphic = image;

            var label = Label("Label", go.transform, text, 34, TextAnchor.MiddleCenter);
            label.color = Color.white;
            SetRect(label.rectTransform, Vector2.zero, Vector2.one, Vector2.zero, Vector2.zero);
            return button;
        }

        static void SetText(Text target, string value)
        {
            if (target != null) target.text = PersianText.Fix(value);
        }

        static string GuideForStep(int step)
        {
            switch(step)
            {
                case 0: return "جوی‌استیک پایین چپ را حرکت بده؛ نشان «هدف» را تا کافه دنبال کن و نزدیک دایره طلایی «تعامل» را بزن.";
                case 1: return "ماموریت بعدی: نشان «هدف» را تا دانشگاه دنبال کن.";
                case 2: return "ماموریت بعدی: به بانک برو و ۲ میلیون تومان پس‌انداز کن.";
                case 3: return "ماموریت بعدی: کنار موتور برو؛ «تعامل» را بزن و آن را بخر.";
                case 4: return "ماموریت بعدی: به ملک خالی برو و اولین کسب‌وکارت را بساز.";
                default: return "شهر باز است؛ کار کن، مهارت بگیر، خرید کن و کسب‌وکارت را بزرگ‌تر کن.";
            }
        }

        static void SetRect(RectTransform rt, Vector2 anchorMin, Vector2 anchorMax, Vector2 anchoredPos, Vector2 size, Vector2? pivot = null)
        {
            rt.anchorMin = anchorMin;
            rt.anchorMax = anchorMax;
            rt.pivot = pivot ?? new Vector2(0.5f, 0.5f);
            rt.anchoredPosition = anchoredPos;
            rt.sizeDelta = size;
        }
    }
}
