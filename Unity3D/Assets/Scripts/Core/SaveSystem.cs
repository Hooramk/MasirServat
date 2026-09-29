using UnityEngine;

namespace MasirServat
{
    public static class SaveSystem
    {
        const string Key = "masir_servat_unity_save_v1";

        public static GameData Load()
        {
            if (!PlayerPrefs.HasKey(Key))
                return new GameData();

            try
            {
                var json = PlayerPrefs.GetString(Key, "");
                var data = JsonUtility.FromJson<GameData>(json);
                return data ?? new GameData();
            }
            catch
            {
                return new GameData();
            }
        }

        public static void Save(GameData data)
        {
            PlayerPrefs.SetString(Key, JsonUtility.ToJson(data));
            PlayerPrefs.Save();
        }

        public static void Clear()
        {
            PlayerPrefs.DeleteKey(Key);
            PlayerPrefs.Save();
        }
    }
}
