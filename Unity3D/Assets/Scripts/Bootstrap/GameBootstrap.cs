using System;
using UnityEngine;
using UnityEngine.EventSystems;

namespace MasirServat
{
    public sealed class GameBootstrap : MonoBehaviour
    {
        [RuntimeInitializeOnLoadMethod(RuntimeInitializeLoadType.AfterSceneLoad)]
        static void AutoBoot()
        {
            if (UnityEngine.Object.FindFirstObjectByType<GameBootstrap>() != null) return;
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
            var cameraGo = CreateCamera();

            Transform player = null;

            try
            {
                player = city.Build();
            }
            catch (Exception ex)
            {
                Debug.LogError("WORLD_BUILD_FATAL\n" + ex);
            }

            if (player == null)
            {
                player = CreateEmergencyPlayer();
                HUDController.I?.Toast("بخشی از شهر بارگذاری نشد؛ کنترل کاراکتر فعال ماند");
            }

            interaction.SetPlayer(player);

            var follow = cameraGo.AddComponent<ThirdPersonCamera>();
            follow.SetTarget(player);

            if (PlayerMotor.I != null)
                PlayerMotor.I.SetCamera(cameraGo.transform);

            gameObject.AddComponent<ObjectiveBeacon>();

            HUDController.I.Refresh();
            HUDController.I.Toast("به مسیر ثروت خوش اومدی\nنشان هدف را تا کافه دنبال کن");
        }

        GameObject CreateCamera()
        {
            var existing = Camera.main;
            if (existing != null)
            {
                existing.clearFlags = CameraClearFlags.SolidColor;
                existing.backgroundColor = new Color(0.47f, 0.68f, 0.86f);
                existing.fieldOfView = 62;
                existing.nearClipPlane = 0.15f;
                existing.farClipPlane = 300;
                return existing.gameObject;
            }

            var cameraGo = new GameObject("Main Camera");
            var camera = cameraGo.AddComponent<Camera>();
            camera.tag = "MainCamera";
            camera.fieldOfView = 62;
            camera.nearClipPlane = 0.15f;
            camera.farClipPlane = 300;
            camera.clearFlags = CameraClearFlags.SolidColor;
            camera.backgroundColor = new Color(0.47f, 0.68f, 0.86f);
            cameraGo.transform.position = new Vector3(-13.8f, 7.5f, -12f);
            cameraGo.transform.rotation = Quaternion.Euler(20f, 0f, 0f);
            cameraGo.AddComponent<AudioListener>();
            return cameraGo;
        }

        Transform CreateEmergencyPlayer()
        {
            var root = new GameObject("Player");
            root.transform.position = new Vector3(-13.8f, 0.05f, -2.5f);

            var controller = root.AddComponent<CharacterController>();
            controller.height = 1.8f;
            controller.radius = 0.38f;
            controller.center = new Vector3(0, 0.9f, 0);

            var body = GameObject.CreatePrimitive(PrimitiveType.Capsule);
            body.name = "EmergencyBody";
            body.transform.SetParent(root.transform, false);
            body.transform.localPosition = new Vector3(0, 0.9f, 0);
            body.transform.localScale = new Vector3(0.72f, 0.9f, 0.72f);
            var collider = body.GetComponent<Collider>();
            if (collider != null) UnityEngine.Object.Destroy(collider);

            root.AddComponent<PlayerMotor>();
            return root.transform;
        }

        void EnsureEventSystem()
        {
            if (UnityEngine.Object.FindFirstObjectByType<EventSystem>() != null) return;
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
