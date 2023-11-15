package course.examples.movieappphase2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import course.examples.movieappphase2.R;
import course.examples.movieappphase2.model.ReviewResult;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder>{

    private Context mContext;
    private List<ReviewResult> reviewResults;

    public ReviewAdapter(Context mContext, List<ReviewResult> reviewResults) {
        this.mContext = mContext;
        this.reviewResults = reviewResults;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_item, viewGroup, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder viewHolder, int i){
        viewHolder.reviewAuthor.setText(reviewResults.get(i).getAuthor());
        String reviewContentStr = reviewResults.get(i).getContent();
        viewHolder.reviewContent.setText(reviewContentStr);

        if (reviewContentStr.length() < 200) {
            viewHolder.getChevron().setVisibility(View.GONE);
        } else
            viewHolder.reviewContent.setInterpolator(new OvershootInterpolator());
    }

    @Override
    public int getItemCount(){
        return reviewResults.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ExpandableTextView reviewContent;
        private TextView reviewAuthor;
        private final View expandCollapseChevron;

        private MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            reviewAuthor = view.findViewById(R.id.tv_review_author);
            reviewContent = view.findViewById(R.id.etv_review_content);
            expandCollapseChevron = view.findViewById(R.id.ic_chevron);
        }

        private View getChevron() {
            return expandCollapseChevron;
        }

        @Override
        public void onClick(View view) {
            expandCollapseChevron.setBackgroundResource(reviewContent.isExpanded() ? R.drawable.ic_chevron_down : R.drawable.ic_chevron_up);
            reviewContent.toggle();
        }
    }
}
