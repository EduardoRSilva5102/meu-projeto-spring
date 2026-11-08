import unittest
from unittest.mock import patch

import deploy_render as deploy


class DeployTests(unittest.TestCase):
    @patch.dict("os.environ", {}, clear=True)
    @patch.object(deploy, "request_json")
    def test_missing_configuration_does_not_trigger_deploy(self, request):
        with self.assertRaisesRegex(RuntimeError, "Configure"):
            deploy.main()
        request.assert_not_called()

    @patch.object(deploy.time, "sleep")
    @patch.object(deploy, "request_json")
    def test_waits_until_expected_commit_is_live(self, request, sleep):
        request.side_effect = [{"status": "build_in_progress"},
                               {"status": "live", "commit": {"id": "abc"}}]
        deploy.wait_for_deploy("https://example.test", "token", "abc")
        self.assertEqual(request.call_count, 2)

    @patch.object(deploy, "request_json", return_value={"status": "build_failed"})
    def test_failed_build_is_not_success(self, request):
        with self.assertRaisesRegex(RuntimeError, "build_failed"):
            deploy.wait_for_deploy("https://example.test", "token", "abc")

    @patch.object(deploy, "request_json", return_value={"status": "live", "commit": {"id": "other"}})
    def test_wrong_commit_is_not_success(self, request):
        with self.assertRaisesRegex(RuntimeError, "commit publicado"):
            deploy.wait_for_deploy("https://example.test", "token", "abc")

    def test_timeout_is_not_success(self):
        with self.assertRaises(TimeoutError):
            deploy.wait_for_deploy("https://example.test", "token", "abc", timeout=0)
