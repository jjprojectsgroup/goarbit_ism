package app.aplicacion.goarbit_ism.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.goarbit.goarbit_ism.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment {
    WebView webView;

    private FragmentSlideshowBinding binding;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

     

        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);
        System.out.println("onCreateView() called  slideshowViewModel ---------------------------------------------------------");

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        return root;

    }

    @Override
    public void onPause() {
        super.onPause();
        binding = null;
    }
}