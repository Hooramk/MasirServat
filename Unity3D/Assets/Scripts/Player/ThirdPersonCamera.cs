using UnityEngine;

namespace MasirServat
{
    public sealed class ThirdPersonCamera : MonoBehaviour
    {
        Transform target;
        Vector3 velocity;
        public Vector3 offset = new Vector3(0, 6.5f, -8.5f);
        public float smoothTime = 0.12f;

        public void SetTarget(Transform t)
        {
            target = t;
            transform.position = target.position + offset;
            transform.LookAt(target.position + Vector3.up * 1.2f);
        }

        void LateUpdate()
        {
            if (target == null) return;

            float yaw = target.eulerAngles.y;
            Quaternion rotation = Quaternion.Euler(0, yaw, 0);
            Vector3 wanted = target.position + rotation * offset;
            transform.position = Vector3.SmoothDamp(transform.position, wanted, ref velocity, smoothTime);
            transform.LookAt(target.position + Vector3.up * 1.25f);
        }
    }
}
