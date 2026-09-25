using UnityEngine;
using UnityEngine.UI;

namespace MasirServat
{
    public sealed class JobMinigame : MonoBehaviour
    {
        public static JobMinigame I { get; private set; }
        public bool IsActive { get; private set; }

        GameObject panel;
        RectTransform track, target, marker;
        Text roundText, scoreText;
        Button stopButton;

        string jobId;
        string jobTitle;
        long basePay;
        int round;
        int totalScore;
        float marker01;
        float speed;
        float targetCenter;
        bool moving;

        void Awake() => I = this;

        public void Begin(string id, string title, long pay)
        {
            if (IsActive) return;

            if (!EconomySystem.I.TrySpend(0, 1))
            {
                HUDController.I?.Toast("انرژی کافی نداری");
                return;
            }

            jobId = id;
            jobTitle = title;
            basePay = pay;
            round = 1;
            totalScore = 0;
            IsActive = true;
            MobileInput.Move = Vector2.zero;

            BuildUI();
            StartRound();
        }

        void Update()
        {
            if (!IsActive || !moving || marker == null) return;

            marker01 = Mathf.PingPong(Time.unscaledTime * speed, 1f);
            float width = track.rect.width;
            marker.anchoredPosition = new Vector2((marker01 - 0.5f) * width, 0);

            if (Input.GetKeyDown(KeyCode.Space))
                StopRound();
        }

        void BuildUI()
        {
            var canvas = HUDController.I.GetCanvas();

            panel = new GameObject("JobMinigamePanel", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            panel.transform.SetParent(canvas.transform, false);
            var prt = panel.GetComponent<RectTransform>();
            prt.anchorMin = new Vector2(0.5f, 0.5f);
            prt.anchorMax = new Vector2(0.5f, 0.5f);
            prt.pivot = new Vector2(0.5f, 0.5f);
            prt.sizeDelta = new Vector2(760, 420);
            panel.GetComponent<Image>().color = new Color(0.025f, 0.07f, 0.12f, 0.97f);

            roundText = MakeText(panel.transform, jobTitle, 38, new Vector2(0, 142), new Vector2(650, 60));

            var trackGo = new GameObject("Track", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            trackGo.transform.SetParent(panel.transform, false);
            track = trackGo.GetComponent<RectTransform>();
            track.anchorMin = track.anchorMax = new Vector2(0.5f, 0.5f);
            track.sizeDelta = new Vector2(610, 72);
            track.anchoredPosition = new Vector2(0, 44);
            trackGo.GetComponent<Image>().color = new Color(0.25f, 0.28f, 0.31f, 1);

            var targetGo = new GameObject("Target", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            targetGo.transform.SetParent(trackGo.transform, false);
            target = targetGo.GetComponent<RectTransform>();
            target.anchorMin = target.anchorMax = new Vector2(0.5f, 0.5f);
            target.sizeDelta = new Vector2(120, 72);
            targetGo.GetComponent<Image>().color = new Color(0.79f, 0.64f, 0.35f, 1);

            var markerGo = new GameObject("Marker", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image));
            markerGo.transform.SetParent(trackGo.transform, false);
            marker = markerGo.GetComponent<RectTransform>();
            marker.anchorMin = marker.anchorMax = new Vector2(0.5f, 0.5f);
            marker.sizeDelta = new Vector2(18, 102);
            markerGo.GetComponent<Image>().color = Color.white;

            scoreText = MakeText(panel.transform, "نشانگر را داخل بخش طلایی متوقف کن", 27, new Vector2(0, -45), new Vector2(650, 55));

            stopButton = MakeButton(panel.transform, "توقف", new Vector2(0, -132), new Vector2(260, 82));
            stopButton.onClick.AddListener(StopRound);
        }

        void StartRound()
        {
            targetCenter = Random.Range(0.25f, 0.75f);
            speed = Random.Range(0.48f, 0.72f) + round * 0.08f;
            marker01 = 0;
            moving = true;

            float width = track.rect.width > 0 ? track.rect.width : 610;
            target.anchoredPosition = new Vector2((targetCenter - 0.5f) * width, 0);
            roundText.text = jobTitle + "  ·  راند " + round + " از ۳";
            scoreText.text = "در لحظه مناسب توقف را بزن";
            stopButton.interactable = true;
        }

        void StopRound()
        {
            if (!IsActive || !moving) return;
            moving = false;

            float distance = Mathf.Abs(marker01 - targetCenter);
            int score = Mathf.Clamp(Mathf.RoundToInt(100 - distance * 185f), 20, 100);
            totalScore += score;
            scoreText.text = "امتیاز راند: " + score;

            if (round >= 3)
            {
                Finish();
                return;
            }

            round++;
            stopButton.interactable = false;
            Invoke(nameof(StartRound), 0.65f);
        }

        void Finish()
        {
            int average = totalScore / 3;
            var d = GameState.I.Data;
            long skillBonus = d.skill * 4_500L;
            long laptopBonus = d.laptopLevel * 130_000L;
            long reward = (long)(basePay * (0.55f + average / 100f * 0.85f)) + skillBonus + laptopBonus;
            int xp = Mathf.Clamp(average / 4, 10, 25);

            EconomySystem.I.Earn(reward, xp);
            if (jobId == "cafe") QuestSystem.I?.Notify("cafe_job");

            HUDController.I?.Toast(
                "شیفت تمام شد\nعملکرد " + average + "/۱۰۰\n+" + EconomySystem.FormatMoney(reward) + " تومان"
            );

            IsActive = false;
            moving = false;
            if (panel != null) Destroy(panel);
            HUDController.I?.Refresh();
        }

        Text MakeText(Transform parent, string value, int size, Vector2 pos, Vector2 box)
        {
            var go = new GameObject("Text", typeof(RectTransform), typeof(CanvasRenderer), typeof(Text));
            go.transform.SetParent(parent, false);
            var rt = go.GetComponent<RectTransform>();
            rt.anchorMin = rt.anchorMax = new Vector2(0.5f, 0.5f);
            rt.anchoredPosition = pos;
            rt.sizeDelta = box;

            var t = go.GetComponent<Text>();
            t.font = UIFontProvider.Get();
            t.text = value;
            t.fontSize = size;
            t.color = Color.white;
            t.alignment = TextAnchor.MiddleCenter;
            t.resizeTextForBestFit = true;
            t.resizeTextMinSize = 18;
            t.resizeTextMaxSize = size;
            return t;
        }

        Button MakeButton(Transform parent, string value, Vector2 pos, Vector2 box)
        {
            var go = new GameObject("StopButton", typeof(RectTransform), typeof(CanvasRenderer), typeof(Image), typeof(Button));
            go.transform.SetParent(parent, false);
            var rt = go.GetComponent<RectTransform>();
            rt.anchorMin = rt.anchorMax = new Vector2(0.5f, 0.5f);
            rt.anchoredPosition = pos;
            rt.sizeDelta = box;
            go.GetComponent<Image>().color = new Color(0.79f, 0.64f, 0.35f, 1);

            var b = go.GetComponent<Button>();
            var text = MakeText(go.transform, value, 32, Vector2.zero, box);
            text.raycastTarget = false;
            return b;
        }
    }
}
