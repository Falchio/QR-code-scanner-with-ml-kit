package ru.piteravto.barcodescanner.permission


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ru.piteravto.barcodescanner.R
import ru.piteravto.barcodescanner.databinding.FragmentPermissionBinding


private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.CAMERA,
)

private const val TAG = "PermissionFragment"

class PermissionFragment : Fragment() {
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!
    private var alertDialog: AlertDialog? = null

    /** Запрос разрешений осуществляется через ActivityResultLauncher */
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val granted = it.value
                val permission = it.key
                if (!granted) {
                    //проверяем запретил ли пользователь повторный запрос прав
                    val doNotAskAgain = !ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        permission
                    )
                    if (doNotAskAgain) {
                        showFatalErrorDialog()
                    } else {
                        showRationaleDialog()
                    }
                    return@registerForActivityResult
                }
            }
            goHome()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        /*Вызывать ранее не нужно, будет падать на функции перехода на основной экран, так как Navigation Host View еще не существует
        *
        * Нельзя вызывать позже так как, в onResume может упасть в мертвый цикл запроса разрешений, если пользователь поставил 'don`t ask again'
        * Дело в том, что после показа диалога с требованием выдать разрешения фрагмент снова переходит в состояние onResume. */
        checkAppPermission()
    }

    private fun goHome() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(R.id.nav_scanner)
    }

    /** Проверяем наличие необходимых разрешений */
    private fun checkAppPermission() {
        PERMISSIONS_REQUIRED.forEach { permission ->
            if (ContextCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_DENIED
            ) {
                requestMultiplePermissions.launch(PERMISSIONS_REQUIRED)
                return
            }
        }
        goHome()
    }

    /** Уничтожаем диалог, если существует для устранения утечек памяти */
    override fun onDestroy() {
        super.onDestroy()
        destroyAlertDialogIfExists()
    }


    /** Метод уничтожает диалоговое окно, если оно существует, для избежания утечек памяти */
    private fun destroyAlertDialogIfExists() {
        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
            alertDialog = null
        }
    }

    /** Отображается диалог, если пользователь отказал в выдаче разрешений
     * и запретил их запрашиваться снова */
    private fun showFatalErrorDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.you_banned_wanted_permissions))
            setMessage(getString(R.string.please_give_app_wanted_permissions))
            setCancelable(false)
            setNeutralButton(getString(R.string.clear_well)) { dialog, _ ->
                dialog.dismiss()
                requireActivity().finish()
            }
        }
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    /** Если пользователь отказал в выдаче разрешения ему будет показан диалог,
     * поясняющий, что дальнейшая работа невозможна без разрешений */
    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.you_dont_give_wanted_permissions))
            setMessage(getString(R.string.you_need_handle_permissions))
            setCancelable(false)
            setNeutralButton(getString(R.string.clear_well)) { dialog, _ ->
                dialog.dismiss()
                checkAppPermission()
            }
        }
        alertDialog = builder.create()
        alertDialog!!.show()
    }

}