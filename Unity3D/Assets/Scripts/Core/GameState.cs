using System;
using UnityEngine;

namespace MasirServat
{
    [Serializable]
    public class GameData
    {
        public string playerName = "بازیکن";
        public long cash = 4_000_000;
        public long savings = 0;
        public long debt = 0;
        public int energy = 5;
        public int maxEnergy = 5;
        public int skill = 20;
        public int reputation = 10;
        public int day = 1;
        public int careerLevel = 1;
        public int jobXp = 0;
        public int transportLevel = 0;
        public int laptopLevel = 0;
        public int phoneLevel = 1;
        public int roomLevel = 1;
        public int businessLevel = 0;
        public int questStep = 0;
        public int followers = 80;
        public bool hasScooter = false;
        public bool tutorialSeen = false;
    }

    public sealed class GameState : MonoBehaviour
    {
        public static GameState I { get; private set; }
        public GameData Data { get; private set; } = new GameData();

        public event Action Changed;

        void Awake()
        {
            if (I != null && I != this)
            {
                Destroy(gameObject);
                return;
            }

            I = this;
            DontDestroyOnLoad(gameObject);
            Data = SaveSystem.Load();
        }

        public void NotifyChanged(bool save = true)
        {
            if (save) SaveSystem.Save(Data);
            Changed?.Invoke();
        }

        public void ResetGame()
        {
            Data = new GameData();
            SaveSystem.Save(Data);
            Changed?.Invoke();
        }
    }
}
