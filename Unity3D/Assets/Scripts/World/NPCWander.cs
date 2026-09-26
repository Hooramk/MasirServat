using UnityEngine;

namespace MasirServat
{
    public sealed class NPCWander : MonoBehaviour
    {
        Vector3 origin;
        Vector3 target;
        float speed;

        void Start()
        {
            origin = transform.position;
            speed = Random.Range(0.7f, 1.3f);
            PickTarget();
        }

        void PickTarget()
        {
            Vector2 p = Random.insideUnitCircle * 7f;
            target = origin + new Vector3(p.x, 0, p.y);
        }

        void Update()
        {
            Vector3 flat = target - transform.position;
            flat.y = 0;

            if (flat.magnitude < 0.4f)
            {
                PickTarget();
                return;
            }

            Vector3 dir = flat.normalized;
            transform.position += dir * speed * Time.deltaTime;
            transform.rotation = Quaternion.Slerp(
                transform.rotation,
                Quaternion.LookRotation(dir),
                5f * Time.deltaTime
            );
        }
    }
}
