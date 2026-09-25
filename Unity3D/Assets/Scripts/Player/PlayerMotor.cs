using UnityEngine;

namespace MasirServat
{
    [RequireComponent(typeof(CharacterController))]
    public sealed class PlayerMotor : MonoBehaviour
    {
        public static PlayerMotor I { get; private set; }

        CharacterController controller;
        Transform cameraTransform;
        float verticalVelocity;
        bool mounted;
        VehicleMount mountedVehicle;

        public float walkSpeed = 4.5f;
        public float rideSpeed = 9f;
        public bool IsMounted => mounted;

        void Awake()
        {
            I = this;
            controller = GetComponent<CharacterController>();
        }

        public void SetCamera(Transform cam) => cameraTransform = cam;

        void Update()
        {
            Vector2 move = MobileInput.Move;
            if (move.sqrMagnitude < 0.01f)
                move = new Vector2(Input.GetAxisRaw("Horizontal"), Input.GetAxisRaw("Vertical"));

            Vector3 forward = cameraTransform != null ? cameraTransform.forward : Vector3.forward;
            Vector3 right = cameraTransform != null ? cameraTransform.right : Vector3.right;
            forward.y = 0;
            right.y = 0;
            forward.Normalize();
            right.Normalize();

            Vector3 desired = forward * move.y + right * move.x;
            if (desired.sqrMagnitude > 1f) desired.Normalize();

            float speed = mounted ? rideSpeed : walkSpeed;
            if (desired.sqrMagnitude > 0.01f)
            {
                transform.rotation = Quaternion.Slerp(
                    transform.rotation,
                    Quaternion.LookRotation(desired),
                    12f * Time.deltaTime
                );
            }

            if (controller.isGrounded && verticalVelocity < 0) verticalVelocity = -1f;
            verticalVelocity += Physics.gravity.y * Time.deltaTime;

            Vector3 velocity = desired * speed;
            velocity.y = verticalVelocity;
            controller.Move(velocity * Time.deltaTime);

            if (Input.GetKeyDown(KeyCode.E))
                WorldInteraction.I?.Interact();

            if (Input.GetKeyDown(KeyCode.R) && mounted)
                Dismount();
        }

        public void Mount(VehicleMount vehicle)
        {
            if (mounted) return;
            mounted = true;
            mountedVehicle = vehicle;
            vehicle.AttachTo(transform);
            HUDController.I?.Toast("سوار شدی · سرعت حرکت بیشتر شد");
        }

        public void Dismount()
        {
            if (!mounted || mountedVehicle == null) return;
            mountedVehicle.DetachAt(transform.position - transform.right * 1.4f);
            mountedVehicle = null;
            mounted = false;
            HUDController.I?.Toast("از موتور پیاده شدی");
        }
    }
}
