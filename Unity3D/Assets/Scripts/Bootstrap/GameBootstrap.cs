using UnityEngine;
using UnityEngine.EventSystems;

namespace MasirServat
{
    public sealed class GameBootstrap : MonoBehaviour
    {
        [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.AfterSceneLoad)]
        static void AutoBoot()
        {
            if (Object.FindFirstObjectByType<GameBootstrap>() != null) return;
            new GameObject("GameBootstrap").AddComponent<GameBootstrap>();
        }

        void Awake()
        {
            DontDestroyOnLoad(gameObject);
            Application.targetFrameRate = 60;
            Screen.orientation = ScreenOrientation.LandscapeLeft;

            EnsureEventSystem();

            gameObject.AddComponent<GameState>();
            gameObject.AddComponent<EconomySystem>();
            gameObject.AddComponent<QuestSystem>();
            gameObject.AddComponent<HUDController>();
            gameObject.AddComponent<JobMinigame>();
            var interaction = gameObject.AddComponent<WorldInteraction>();
            var city = gameObject.AddComponent<CityPrototypeBuilder>();

            CreateLighting();

            Transform player = city.Build();
            interaction.SetPlayer(player);

            var cameraGo = new GameObject("Main Camera");
            var camera = cameraGo.AddComponent<Camera>();
            camera.tag = "MainCamera";
            camera.fieldOfView = 62;
            camera.nearClipPlane = 0.15f;
            camera.farClipPlane = 300;
            camera.clearFlags = CameraClearFlags.SolidColor;
            camera.backgroundColor = new Color(0.47f, 0.68f, 0.86f);
            cameraGo.AddComponent<AudioListener>();

            var follow = cameraGo.AddComponent<ThirdPersonCamera>();
            follow.SetTarget(player);
            PlayerMotor.I.SetCamera(cameraGo.transform);

            gameObject.AddComponent<ObjectiveBeacon>();

            HUDController.I.Refresh();
            HUDController.I.Toast("به مسیر ثروت خوش اومدی\nبرو کافه و اولین شیفتت رو شروع کن");
        }

        void EnsureEventSystem()
        {
            if (Object.FindFirstObjectByType<EventSystem>() != null) return;
            var eventSystem = new GameObject("EventSystem");
            eventSystem.AddComponent<EventSystem>();
            eventSystem.AddComponent<StandaloneInputModule>();
        }

        void CreateLighting()
        {
            RenderSettings.fog = false;
            RenderSettings.ambientMode = UnityEngine.Rendering.AmbientMode.Trilight;
            RenderSettings.ambientSkyColor = new Color(0.56f, 0.67f, 0.78f);
            RenderSettings.ambientEquatorColor = new Color(0.45f, 0.47f, 0.43f);
            RenderSettings.ambientGroundColor = new Color(0.24f, 0.25f, 0.23f);

            var sunGo = new GameObject("Sun");
            sunGo.transform.rotation = Quaternion.Euler(48, -28, 0);
            var light = sunGo.AddComponent<Light>();
            light.type = LightType.Directional;
            light.intensity = 1.65f;
            light.color = new Color(1f, 0.94f, 0.82f);
            light.shadows = LightShadows.Soft;
        }
    }
}
