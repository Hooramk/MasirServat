using UnityEngine;
using UnityEngine.EventSystems;

namespace MasirServat
{
    public sealed class TouchInputController : MonoBehaviour
    {
        int moveFinger = -1;
        int lookFinger = -1;
        Vector2 moveStart;
        Vector2 lookLast;

        const float MoveRadius = 115f;
        const float LookSensitivity = 0.16f;

        void Update()
        {
            if (JobMinigame.I != null && JobMinigame.I.IsActive)
            {
                ResetMove();
                return;
            }

            if (Input.touchSupported && Input.touchCount > 0)
            {
                HandleTouches();
            }
            else
            {
                HandleMouseFallback();
            }
        }

        void HandleTouches()
        {
            bool moveStillAlive = false;
            bool lookStillAlive = false;

            for (int i = 0; i < Input.touchCount; i++)
            {
                Touch t = Input.GetTouch(i);

                if (t.phase == TouchPhase.Began)
                {
                    bool overUi = EventSystem.current != null &&
                                  EventSystem.current.IsPointerOverGameObject(t.fingerId);

                    if (!overUi && t.position.y < Screen.height * 0.88f)
                    {
                        if (t.position.x < Screen.width * 0.56f && moveFinger < 0)
                        {
                            moveFinger = t.fingerId;
                            moveStart = t.position;
                            moveStillAlive = true;
                            ApplyMove(t.position);
                            HUDController.I?.ShowTouchJoystick(moveStart, t.position, true);
                        }
                        else if (t.position.x >= Screen.width * 0.44f && lookFinger < 0)
                        {
                            lookFinger = t.fingerId;
                            lookLast = t.position;
                            lookStillAlive = true;
                        }
                    }
                }

                if (t.fingerId == moveFinger)
                {
                    moveStillAlive = t.phase != TouchPhase.Ended && t.phase != TouchPhase.Canceled;
                    if (moveStillAlive)
                    {
                        ApplyMove(t.position);
                        HUDController.I?.ShowTouchJoystick(moveStart, t.position, true);
                    }
                }

                if (t.fingerId == lookFinger)
                {
                    lookStillAlive = t.phase != TouchPhase.Ended && t.phase != TouchPhase.Canceled;
                    if (lookStillAlive && t.phase == TouchPhase.Moved)
                    {
                        Vector2 delta = t.position - lookLast;
                        ThirdPersonCamera.I?.AddLook(delta * LookSensitivity);
                        lookLast = t.position;
                    }
                }
            }

            if (moveFinger >= 0 && !moveStillAlive)
                ResetMove();

            if (lookFinger >= 0 && !lookStillAlive)
                lookFinger = -1;
        }

        void ApplyMove(Vector2 current)
        {
            Vector2 delta = current - moveStart;
            MobileInput.Move = Vector2.ClampMagnitude(delta / MoveRadius, 1f);
        }

        void ResetMove()
        {
            moveFinger = -1;
            MobileInput.Move = Vector2.zero;
            HUDController.I?.ShowTouchJoystick(Vector2.zero, Vector2.zero, false);
        }

        void HandleMouseFallback()
        {
            // Desktop/editor convenience: RMB drag rotates camera.
            if (Input.GetMouseButtonDown(1))
                lookLast = Input.mousePosition;

            if (Input.GetMouseButton(1))
            {
                Vector2 now = Input.mousePosition;
                ThirdPersonCamera.I?.AddLook((now - lookLast) * LookSensitivity);
                lookLast = now;
            }
        }

        void OnDisable()
        {
            ResetMove();
            lookFinger = -1;
        }
    }
}
