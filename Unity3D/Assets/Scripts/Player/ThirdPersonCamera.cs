using UnityEngine;

namespace MasirServat
{
    public sealed class ThirdPersonCamera : MonoBehaviour
    {
        Transform target;
        Vector3 velocity;

        // Fixed city-game camera. It does not orbit every time the player turns.
        public Vector3 offset = new Vector3(0f, 12.5f, -14.5f);
        public float smoothTime = 0.10f;

        public void SetTarget(Transform t)
        {
            target = t;
            Snap();
        }

        void Snap()
        {
            if (target == null) return;
            transform.position = target.position + offset;
            transform.LookAt(target.position + Vector3.up * 1.0f);
        }

        void LateUpdate()
        {
            if (target == null) return;

            Vector3 wanted = target.position + offset;
            transform.position = Vector3.SmoothDamp(
                transform.position,
                wanted,
                ref velocity,
                smoothTime
            );
            transform.LookAt(target.position + Vector3.up * 1.0f);
        }
    }
}
