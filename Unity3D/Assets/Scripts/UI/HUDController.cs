using System.Collections;
using UnityEngine;
using UnityEngine.UI;

namespace MasirServat
{
    public sealed class HUDController : MonoBehaviour
    {
        public static HUDController I { get; private set; }

        Canvas canvas;
        Font font;
        Text moneyText, energyText, skillText, reputationText, dayText;
        Text missionText, promptText, toastText, guideText;
        Button interactButton;
        RectTransform touchBase, touchKnob;
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

            BuildStats();
            BuildMission();
            BuildTouchIndicator();

            promptText = Label("Prompt", canvas.transform, "", 25, TextAnchor.MiddleCenter);
            promptText.color = new Color(1f, 0.95f, 0.82f);
            promptText.gameObject.AddComponent<Outline>().effectColor = new Color(0, 0, 0, 0.76f);
            SetRect(promptText.rectTransform, new Vector2(0.64f, 0), new Vector2(0.95f, 0),
                new Vector2(0, 220), new Vector2(0, 54), new Vector2(0.5f, 0));

            guideText = Label("Guide", canvas.transform, "", 22, TextAnchor.MiddleCenter);
            guideText.color = new Color(0.94f, 0.95f, 0.97f, 0.92f);
            guideText.gameObject.AddComponent<Outline>().effectColor = new Color(0, 0, 0, 0.72f);
            SetRect(guideText.rectTransform, new Vector2(0.20f, 0), new Vector2(0.80f, 0),
                new Vector2(0, 54), new Vector2(0, 52), new Vector2(0.5f, 0));

            toastText = Label("Toast", canvas.transform, "", 28, TextAnchor.MiddleCenter);
            toastText.color = Color.white;
            toastText.gameObject.AddComponent<Outline>().effectColor = new Color(0, 0, 0, 0.9f);
            SetRect(toastText.rectTransform, new Vector2(0.30f, 0.5f), new Vector2(0.70f, 0.5f),
                Vector2.zero, new Vector2(0, 145), new Vector2(0.5f, 0.5f));

            interactButton = Button("Interact", canvas.transform, "تعامل", new Color(0.86f, 0.63f, 0.28f, 0.96f));
            var irt = interactButton.GetComponent<RectTransform>();
            SetRect(irt, new Vector2(1, 0), new Vector2(1, 0),
                new Vector2(-132, 142), new Vector2(148, 148), new Vector2(0.5f, 0.5f));

            var buttonImage = interactButton.GetComponent<Image>();
            var round = Resources.GetBuiltinResource<Sprite>("UI/Skin/Knob.psd");
            if (round != null)
            {
                buttonImage.sprite = round;
                buttonImage.preserveAspect = true;
            }

            interactButton.onClick.AddListener(() => WorldInteraction.I?.Interact());
            interactButton.gameObject.SetActive(false);
        }

        void BuildStats()
        {
            var parent = new GameObject("StatCards", typeof(RectTransform));
            parent.transform.SetParent(canvas.transform, false);
            var rt = parent.GetComponent<RectTransform>();
            SetRect(rt, new Vector2(0.5f, 1), new Vector2(0.5f, 1),
                new Vector2(0, -42), new Vector2(1500, 82), new Vector2(0.5f, 1));

            moneyText = StatCard(parent.transform, -590, 280);
            energyText = StatCard(parent.transform, -295, 235);
            skillText = StatCard(parent.transform, -42, 235);
            reputationText = StatCard(parent.transform, 211, 235);
            dayText = StatCard(parent.transform, 475, 205);
        }

        Text StatCard(Transform parent, float x, float width)
        {
            var panel = Panel("Card", parent, new Color(0.025f, 0.055f, 0.09f, 0.88f));
            var prt = panel.rectTransform;
            prt.anchorMin = prt.anchorMax = new Vector2(0.5f, 0.5f);
            prt.pivot = new Vector2(0.5f, 0.5f);
            prt.anchoredPosition = new Vector2(x, 0);
            prt.sizeDelta = new Vector2(width, 66);

            var sprite = Resources.GetBuiltinResource<Sprite>("UI/Skin/Background.psd");
            if (sprite != null)
            {
                panel.sprite = sprite;
                panel.type = Image.Type.Sliced;
            }

            var t = Label("Value", panel.transform, "", 26, TextAnchor.MiddleCenter);
            SetRect(t.rectTransform, Vector2.zero, Vector2.one, Vector2.zero, new Vector2(-18, -8));
            return t;
        }

        void BuildMission()
        {
            var panel = Panel("MissionCard", canvas.transform, new Color(0.025f, 0.055f, 0.09f, 0.86f));
            SetRect(panel.rectTransform, new Vector2(0.5f, 1), new Vector2(0.5f, 1),
                new Vector2(0, -116), new Vector2(920, 72), new Vector2(0.5f, 1));

            var sprite = Resources.GetBuiltinResource<Sprite>("UI/Skin/Background.psd");
            if (sprite != null)
            {
                panel.sprite = sprite;
                panel.type = Image.Type.Sliced;
            }

            var accent = Panel("Accent", panel.transform, new Color(0.86f, 0.63f, 0.28f, 1f));
            SetRect(accent.rectTransform, new Vector2(1, 0), new Vector2(1, 1),
                new Vector2(-5, 0), new Vector2(8, -14), new Vector2(1, 0.5f));

            missionText = Label("Mission", panel.transform, "", 25, TextAnchor.MiddleRight);
            missionText.color = new Color(1f, 0.91f, 0.70f);
            SetRect(missionText.rectTransform, Vector2.zero, Vector2.one,
                new Vector2(-28, 0), new Vector2(-54, -8));
        }

        void BuildTouchIndicator()
        {
            var baseGo = new GameObject("FloatingMoveBase", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            baseGo.transform.SetParent(canvas.transform, false);
            touchBase = baseGo.GetComponent<RectTransform>();
            touchBase.anchorMin = touchBase.anchorMax = new Vector2(0.5f, 0.5f);
            touchBase.pivot = new Vector2(0.5f, 0.5f);
            touchBase.sizeDelta = new Vector2(205, 205);

            var image = baseGo.GetComponent<Image>();
            image.raycastTarget = false;
            image.color = new Color(0.04f, 0.08f, 0.13f, 0.40f);
            var knobSprite = Resources.GetBuiltinResource<Sprite>("UI/Skin/Knob.psd");
            if (knobSprite != null)
            {
                image.sprite = knobSprite;
                image.preserveAspect = true;
            }

            var knobGo = new GameObject("Knob", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            knobGo.transform.SetParent(baseGo.transform, false);
            touchKnob = knobGo.GetComponent<RectTransform>();
            touchKnob.anchorMin = touchKnob.anchorMax = new Vector2(0.5f, 0.5f);
            touchKnob.pivot = new Vector2(0.5f, 0.5f);
            touchKnob.sizeDelta = new Vector2(82, 82);

            var knobImage = knobGo.GetComponent<Image>();
            knobImage.raycastTarget = false;
            knobImage.color = new Color(0.96f, 0.97f, 1f, 0.78f);
            if (knobSprite != null)
            {
                knobImage.sprite = knobSprite;
                knobImage.preserveAspect = true;
            }

            baseGo.SetActive(false);
        }

        public void ShowTouchJoystick(Vector2 startScreen, Vector2 currentScreen, bool visible)
        {
            if (touchBase == null || touchKnob == null || canvas == null) return;

            touchBase.gameObject.SetActive(visible);
            if (!visible) return;

            RectTransform canvasRect = canvas.transform as RectTransform;
            if (canvasRect == null) return;

            if (RectTransformUtility.ScreenPointToLocalPointInRectangle(canvasRect, startScreen, null, out var local))
                touchBase.anchoredPosition = local;

            float scale = Mathf.Max(0.01f, canvas.scaleFactor);
            Vector2 delta = (currentScreen - startScreen) / scale;
            touchKnob.anchoredPosition = Vector2.ClampMagnitude(delta, 78f);
        }

        public void Refresh()
        {
            if (GameState.I == null) return;
            var d = GameState.I.Data;

            SetText(moneyText, "پول  " + EconomySystem.FormatMoney(d.cash));
            SetText(energyText, "انرژی  " + d.energy + "/" + d.maxEnergy);
            SetText(skillText, "مهارت  " + d.skill);
            SetText(reputationText, "اعتبار  " + d.reputation);
            SetText(dayText, "روز  " + d.day);

            if (missionText != null && QuestSystem.I != null)
                SetText(missionText, "ماموریت جاری  •  " + QuestSystem.I.CurrentTitle);

            if (guideText != null)
                SetText(guideText, GuideForStep(d.questStep));
        }

        public void SetPrompt(string text)
        {
            bool active = !string.IsNullOrEmpty(text);

            if (promptText != null)
                SetText(promptText, active ? text : "");

            if (interactButton != null)
            {
                interactButton.gameObject.SetActive(active);
                var label = interactButton.transform.Find("Label")?.GetComponent<Text>();
                if (label != null) SetText(label, "تعامل");
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
            yield return new WaitForSeconds(2.5f);
            toastText.CrossFadeAlpha(0f, 0.35f, true);
            yield return new WaitForSeconds(0.38f);
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
            image.raycastTarget = false;
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
            t.raycastTarget = false;
            t.resizeTextForBestFit = true;
            t.resizeTextMinSize = 17;
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
            image.raycastTarget = true;

            var button = go.GetComponent<Button>();
            button.targetGraphic = image;

            var label = Label("Label", go.transform, text, 28, TextAnchor.MiddleCenter);
            label.color = Color.white;
            SetRect(label.rectTransform, Vector2.zero, Vector2.one, Vector2.zero, new Vector2(-14, -14));
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
                case 0: return "نیمه چپ صفحه: حرکت  •  نیمه راست: چرخاندن دوربین  •  نشان هدف را تا کافه دنبال کن";
                case 1: return "نشان هدف را تا دانشگاه دنبال کن؛ وقتی نزدیک شدی دکمه تعامل ظاهر می‌شود";
                case 2: return "به بانک برو و ۲ میلیون تومان پس‌انداز کن";
                case 3: return "موتور را پیدا کن، نزدیکش شو و تعامل را بزن";
                case 4: return "به ملک خالی برو و اولین کسب‌وکارت را راه بینداز";
                default: return "شهر را بگرد، کار کن، مهارت بگیر و سبک زندگی‌ات را ارتقا بده";
            }
        }

        static void SetRect(RectTransform rt, Vector2 anchorMin, Vector2 anchorMax,
            Vector2 anchoredPos, Vector2 size, Vector2? pivot = null)
        {
            rt.anchorMin = anchorMin;
            rt.anchorMax = anchorMax;
            rt.pivot = pivot ?? new Vector2(0.5f, 0.5f);
            rt.anchoredPosition = anchoredPos;
            rt.sizeDelta = size;
        }
    }
}
