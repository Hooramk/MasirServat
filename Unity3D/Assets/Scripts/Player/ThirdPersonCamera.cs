using UnityEngine;

namespace MasirServat
{
    public sealed class ThirdPersonCamera : MonoBehaviour
    {
        public static ThirdPersonCamera I { get; private set; }

        Transform target;
        Vector3 velocity;
        float yaw = 18f;
        float pitch = 20f;

        public float distance = 6.8f;
        public float smoothTime = 0.08f;

        void Awake() => I = this;

        public void SetTarget(Transform t)
        {
            target = t;
            Snap();
        }

        public void AddLook(Vector2 delta)
        {
            yaw += delta.x;
            pitch = Mathf.Clamp(pitch - delta.y * 0.65f, 12f, 36f);
        }

        void Snap()
        {
            if (target == null) return;
            Vector3 focus = target.position + Vector3.up * 1.25f;
            Quaternion rot = Quaternion.Euler(pitch, yaw, 0);
            transform.position = focus + rot * new Vector3(0, 0, -distance);
            transform.LookAt(focus);
        }

        void LateUpdate()
        {
            if (target == null) return;

            Vector3 focus = target.position + Vector3.up * 1.25f;
            Quaternion rot = Quaternion.Euler(pitch, yaw, 0);
            Vector3 wanted = focus + rot * new Vector3(0, 0, -distance);

            transform.position = Vector3.SmoothDamp(
                transform.position,
                wanted,
                ref velocity,
                smoothTime
            );
            transform.LookAt(focus);
        }
    }
}
