package sdis.wetranslate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;

import sdis.wetranslate.exceptions.ServerRequestException;
import sdis.wetranslate.logic.Translation;
import sdis.wetranslate.logic.User;

import static sdis.wetranslate.logic.ServerRequest.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTranslationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTranslationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTranslationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText textToTranslate;
    private Spinner dropdownFrom = null;
    private Spinner dropdownTo = null;

    public NewTranslationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewTranslationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTranslationFragment newInstance(String param1, String param2) {
        NewTranslationFragment fragment = new NewTranslationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_new_translation, container, false);

        // Decrease edit text's font size
        textToTranslate = (EditText) rootView.findViewById(R.id.textToTranslate);
        textToTranslate.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.result_font));

        // Feed values to spinners
        ArrayList<String> items = Translation.getLanguagesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

        dropdownFrom = (Spinner) rootView.findViewById(R.id.spinnerTranslateFrom);
        dropdownFrom.setAdapter(adapter);

        dropdownTo = (Spinner) rootView.findViewById(R.id.spinnerTranslateTo);
        dropdownTo.setMinimumWidth(dropdownFrom.getWidth());
        dropdownTo.setAdapter(adapter);

        // Listeners
        Button buttonSend = (Button) rootView.findViewById(R.id.buttonTranslateSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToTranslate();
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendToTranslate() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String textTranslate=textToTranslate.getText().toString();
                if (textTranslate.length() != 0){
                    String from=dropdownFrom.getSelectedItem().toString();
                    String to=dropdownTo.getSelectedItem().toString();
                    try {
                        if(insertNewRequest(User.getInstance().getUsername(),Translation.getLanguage(from),Translation.getLanguage(to),textTranslate,getActivity())){
                            Snackbar newRequestPopup=Snackbar.make(getView(),"Pedido de tradução enviado.",Snackbar.LENGTH_SHORT);
                            newRequestPopup.show();
                            EditText editT= (EditText) getView().findViewById(R.id.textToTranslate);
                            editT.setText("");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ServerRequestException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
