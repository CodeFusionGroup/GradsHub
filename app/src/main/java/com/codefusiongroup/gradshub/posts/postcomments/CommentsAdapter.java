package com.codefusiongroup.gradshub.posts.postcomments;

//
//public class CommentsAdapter extends RecyclerView.Adapter<GroupPostCommentsRecyclerViewAdapter.ViewHolder> {
//
//    private LayoutInflater inflater;
//    private ArrayList<Comment> commentsList;
//
////    public class ViewHolder {
////
////        TextView commentDateTV;
////        TextView commentCreatorTV;
////        TextView commentTV;
////
////    }
//
//
//    public CommentsAdapter(Context context, ArrayList<Comment> commentsList) {
//        inflater = LayoutInflater.from(context);
//        this.commentsList = commentsList;
//    }
//
//
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_groups_comment_item, parent, false);
//        return new OpenChatsRecyclerViewAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        holder.mComment = commentsList.get(position);
//
//        holder.mCommentDateView.setText( commentsList.get(position).getCommentDate() );
//        holder.mCommentCreatorView.setText( commentsList.get(position).getCommentCreator() );
//        holder.mCommentView.setText( commentsList.get(position).getComment() );
//
//    }
//
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//
//    @Override
//    public int getItemCount() { return commentsList.size(); }
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        public final View mView;
//        public final TextView mCommentDateView;
//        public final TextView mCommentCreatorView;
//        public final TextView mCommentView;
//        public Comment mComment;
//
//        public ViewHolder(View view) {
//
//            super(view);
//
//            mView = view;
//            mCommentDateView = view.findViewById(R.id.commentDateTV);
//            mCommentCreatorView = view.findViewById(R.id.commentCreatorTV);
//            mCommentView = view.findViewById(R.id.commentTV);
//
//        }
//
//    }
//
//
//
//
////    @Override
////    public View getView(int position, View convertView, ViewGroup parent) {
////
////        ViewHolder holder;
////
////        if(convertView == null) {
////
////            holder = new ViewHolder();
////            convertView = inflater.inflate(R.layout.fragment_my_groups_comment_item, null);
////
////            holder.commentDateTV = convertView.findViewById(R.id.commentDateTV);
////            holder.commentCreatorTV = convertView.findViewById(R.id.commentCreatorTV);
////            holder.commentTV = convertView.findViewById(R.id.commentTV);
////
////            convertView.setTag(holder);
////
////        }
////
////        else {
////            holder = (ViewHolder) convertView.getTag();
////        }
////
////        holder.commentDateTV.setText(commentsList.get(position).getCommentDate());
////        holder.commentCreatorTV.setText(commentsList.get(position).getCommentCreator());
////        holder.commentTV.setText(commentsList.get(position).getComment());
////
////        return convertView;
////
////    }
//
//}
